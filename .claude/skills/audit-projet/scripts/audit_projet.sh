#!/usr/bin/env bash
# Audit automatique d'un projet Android/Kotlin contre la grille d'évaluation (références : audit-projet/references/grille.md, traduite pour ce projet dans CLAUDE.md).
# Lecture seule : ne modifie rien. Usage : audit_projet.sh [racine-du-projet]
#
# Chaque ligne = un critère : OK (preuve trouvée), A VERIFIER (indice faible ou à relire), MANQUE (écart net).
# Ce script ne donne PAS de verdict final : seul un relecteur (humain ou Claude, preuves à l'appui) conclut.
set -uo pipefail
cd "${1:-.}" || { echo "racine introuvable" >&2; exit 2; }

ok()   { printf '%-18s OK           %s\n' "$1" "$2"; }
warn() { printf '%-18s A VERIFIER   %s\n' "$1" "$2"; }
bad()  { printf '%-18s MANQUE       %s\n' "$1" "$2"; }
count() { [ -z "$1" ] && echo 0 || printf '%s\n' "$1" | wc -l | tr -d ' '; }
files() { find . -name "$1" -not -path '*/build/*' -not -path '*/.gradle/*' -not -path '*/.git/*' "${@:2}" 2>/dev/null; }

# Seuils : 60 % de fichiers avec KDoc balisé (les composants simples n'en ont pas besoin) ; 350 lignes = au-delà, un écran mélange trop de responsabilités.
KT=$(files '*.kt' -path '*/src/main/*')
[ -z "$KT" ] && { echo "Aucun fichier Kotlin sous src/main : mauvaise racine ?" >&2; exit 2; }
DOMAIN=$(printf '%s\n' "$KT" | grep '/domain/' || true)
PRES=$(printf '%s\n' "$KT" | grep -E '/features/|/ui/' || true)
DATA=$(printf '%s\n' "$KT" | grep '/data/' || true)
GRADLE=$(files 'build.gradle.kts')
MANIFESTS=$(files 'AndroidManifest.xml' -path '*/src/*')

echo "== Architecture"
for d in data domain; do
  [ "$(count "$(printf '%s\n' "$KT" | grep "/$d/" || true)")" -gt 0 ] || bad "Couches" "aucun dossier/package '$d'"
done
[ -n "$PRES" ] || bad "Couches" "aucun dossier/package de présentation (presentation, features ou ui)"
v=$(printf '%s\n' $DOMAIN | xargs -r grep -nE '^import (android|androidx|retrofit2|okhttp3|io\.ktor|io\.realm|com\.google)' 2>/dev/null | head -5)
[ -z "$v" ] && ok "Domain" "domain sans import Android/Ktor/Realm ($(count "$DOMAIN") fichiers)" || { bad "Domain" "imports interdits dans domain :"; echo "$v" | sed 's/^/                                /'; }
v=$(printf '%s\n' $PRES | xargs -r grep -nE '^import .*\.data\.' 2>/dev/null | head -5)
[ -z "$v" ] && ok "Couches" "presentation n'importe jamais data" || { bad "Couches" "presentation importe data :"; echo "$v" | sed 's/^/                                /'; }
n=$(printf '%s\n' $DOMAIN | xargs -r grep -lE 'interface \w+Repository' 2>/dev/null | wc -l | tr -d ' ')
[ "$n" -gt 0 ] && ok "Domain" "$n interface(s) Repository dans domain" || bad "Domain" "aucune interface Repository dans domain"
bld=$(grep -rhoiE 'hilt|dagger|koin' $(files 'libs.versions.toml') $GRADLE 2>/dev/null | tr 'A-Z' 'a-z' | sort -u | tr '\n' ' ')
case "$bld" in *koin*) ok "Injection" "Koin déclaré dans les dépendances";; *) bad "Injection" "Koin absent des dépendances (obligatoire)";; esac
hl=$(printf '%s\n' $KT | xargs -r grep -nE '@HiltViewModel|@AndroidEntryPoint|@HiltAndroidApp|@Inject\b|hiltViewModel\(' 2>/dev/null | head -3)
case "$bld" in *hilt*|*dagger*) bad "Injection" "Hilt/Dagger déclaré dans Gradle : interdit";; esac
[ -z "$hl" ] && ok "Injection" "aucune annotation Hilt/Dagger dans le code" || { bad "Injection" "annotations Hilt/Dagger trouvées (interdites) :"; echo "$hl" | cut -c1-110 | sed 's/^/                                /'; }
[ -n "$(printf '%s\n' $KT | xargs -r grep -lE 'startKoin|module \{|koinViewModel|viewModel \{' 2>/dev/null)" ] && ok "Injection" "modules Koin et démarrage trouvés" || bad "Injection" "aucun module Koin / startKoin trouvé"

echo "== Qualité du code"
withdoc=$(printf '%s\n' $DOMAIN $DATA $PRES | xargs -r grep -l '/\*\*' 2>/dev/null)
withtag=$(printf '%s\n' $withdoc | xargs -r grep -lE '^\s*\* @(param|return|property|receiver|throws|see|constructor)' 2>/dev/null)
nd=$(count "$withdoc"); nt=$(count "$withtag")
[ "$nd" -gt 0 ] && [ $((nt * 100 / nd)) -ge 60 ] && ok "KDoc" "KDoc avec balises : $nt fichier(s) sur $nd" || warn "KDoc" "KDoc avec balises : $nt fichier(s) sur $nd (viser les composants complexes)"
cat=$(printf '%s\n' $DATA | xargs -r grep -l 'catch (e: IOException)' 2>/dev/null)
[ "$(count "$cat")" -le 1 ] && ok "Duplication" "pas de bloc try/catch réseau dupliqué" || warn "Duplication" "bloc try/catch IOException recopié dans $(count "$cat") fichiers : factoriser ou justifier"
big=$(printf '%s\n' $PRES | xargs -r wc -l 2>/dev/null | awk '$1>350 && $2!="total"{print "  "$2" ("$1" lignes)"}')
[ -z "$big" ] && ok "UI" "aucun fichier de présentation de plus de 350 lignes" || { warn "UI" "fichiers longs à découper en composables :"; echo "$big" | head -5 | sed 's/^/                                /'; }
nst=$(count "$(printf '%s\n' $PRES | xargs -r grep -lE '(data|sealed) (class|interface) \w*State\b' 2>/dev/null)"); [ "$nst" -gt 0 ] && ok "UI" "$nst fichier(s) State" || bad "UI" "aucune classe State"
hv=$(printf '%s\n' $GRADLE | xargs -r grep -nE '"[A-Za-z0-9._-]+:[A-Za-z0-9._-]+:[0-9]' 2>/dev/null | head -3)
[ -z "$hv" ] && ok "Gradle" "aucune version en dur dans les build.gradle.kts" || { bad "Gradle" "version en dur :"; echo "$hv" | sed 's/^/                                /'; }
if [ -n "$(files 'libs.versions.toml')" ]; then ok "Gradle" "catalogue libs.versions.toml présent"
elif grep -qE '^\w+Version\s*=' gradle.properties 2>/dev/null; then warn "Gradle" "versions centralisées dans gradle.properties (acceptable si les noms sont explicites ; un libs.versions.toml est préférable)"
else bad "Gradle" "versions non centralisées (ni libs.versions.toml ni gradle.properties)"; fi
nr=$(printf '%s\n' $GRADLE | xargs -r grep -L 'region' 2>/dev/null | wc -l | tr -d ' ')
[ "$nr" -eq 0 ] && ok "Gradle" "tous les build.gradle.kts ont des // region" || warn "Gradle" "$nr build.gradle.kts sans // region"
nc=$(printf '%s\n' $MANIFESTS | xargs -r grep -c '<!--' 2>/dev/null | awk -F: '{s+=$NF}END{print s+0}')
[ "$nc" -gt 0 ] && ok "Manifest" "Manifest commenté ($nc commentaires)" || bad "Manifest" "Manifest sans commentaire explicatif"
nl=$(count "$(printf '%s\n' $KT | xargs -r grep -liE '(cycle de vie|lifecycle).*' 2>/dev/null | xargs -r grep -nE '//.*([Cc]ycle de vie|[Ll]ifecycle)' 2>/dev/null)")
[ "$nl" -gt 0 ] && ok "Cycle de vie" "$nl commentaire(s) sur le cycle de vie" || bad "Cycle de vie" "aucun commentaire « Cycle de vie : » (à mettre en avant)"
ext=$(printf '%s\n' $KT | xargs -r grep -nE '^(internal |private )?fun (Context|ViewModel|Intent|Activity|ComponentActivity)\.' 2>/dev/null)
extctx=$(echo "$ext" | grep -E 'fun Context\.')
[ -n "$extctx" ] && ok "Extension" "extension de Context : $(echo "$extctx" | head -1 | sed 's/^.*fun //' | cut -c1-50)" || bad "Extension" "aucune fonction d'extension de Context (exigée) ; autres extensions : ${ext:-aucune}"
nint=$(count "$(printf '%s\n' $KT | xargs -r grep -lE '\b(Intent\(|PendingIntent\.)' 2>/dev/null | xargs -r grep -LE 'sealed (interface|class) \w*Intent' 2>/dev/null)")
[ "$nint" -gt 0 ] && ok "Intent Android" "$nint fichier(s) utilisent un Intent Android" || warn "Intent Android" "aucun Intent Android trouvé (explicite ou implicite) : à démontrer"
mgr=$(printf '%s\n' $KT | xargs -r grep -hE '(class|interface|object) \w+Manager' 2>/dev/null | sed -E 's/.*(class|interface|object) (\w+Manager).*/\2/' | sort -u | tr '\n' ' ')
[ -n "$mgr" ] && ok "Managers" "Managers : $mgr" || bad "Managers" "aucun Manager pour les capacités natives"
warn "Avertissements" "avertissements : lancer ./gradlew lintDebug et lire les sorties de compilation (non exécuté ici)"

echo "== Front-end"
ndao=$(count "$(printf '%s\n' $KT | xargs -r grep -lE '@Dao|class \w+Local\b' 2>/dev/null)"); nent=$(count "$(printf '%s\n' $KT | xargs -r grep -lE '@Entity|: RealmObject' 2>/dev/null)")
ndto=$(count "$(printf '%s\n' $DATA | grep -E 'Dto|Response' || true)"); nsvc=$(count "$(printf '%s\n' $DATA | xargs -r grep -lE '@(GET|POST|PUT|DELETE|PATCH)|client\s*\.get\(|class \w+Api\b' 2>/dev/null)")
[ "$ndao" -gt 0 ] && [ "$nent" -gt 0 ] && ok "Data" "DAO/Entity ($ndao/$nent) et Service/DTO ($nsvc/$ndto)" || warn "Data" "Service/DTO : $nsvc/$ndto ; DAO/Entity : $ndao/$nent (le critère attend les deux couples et un fetch local + distant)"
nmap=$(count "$( (printf '%s\n' $DATA | grep '/mapper/' ; printf '%s\n' $DATA | xargs -r grep -lE 'fun \w+(<[^>]*>)?\.to\w+\(' 2>/dev/null) | sort -u | grep . || true)"); [ "$nmap" -gt 0 ] && ok "Data" "$nmap fichier(s) de mapping" || bad "Data" "aucun mapper entre les couches"
act=$(printf '%s\n' $KT | xargs -r grep -lE ':\s*(ComponentActivity|AppCompatActivity|FragmentActivity)\(\)' 2>/dev/null)
[ "$(count "$act")" -eq 1 ] && ok "Navigation" "Single Activity ($(basename "$act"))" || warn "Navigation" "$(count "$act") activité(s) trouvée(s) : le projet exige une Single Activity"
[ -n "$(printf '%s\n' $KT | xargs -r grep -lE 'NavHost|NavDisplay' 2>/dev/null)" ] && ok "Navigation" "navigation Compose (NavHost ou NavDisplay)" || bad "Navigation" "ni NavHost ni NavDisplay"
nin=$(count "$(printf '%s\n' $PRES | xargs -r grep -lE 'sealed (interface|class) \w*Intent\b' 2>/dev/null)"); nef=$(count "$(printf '%s\n' $PRES | xargs -r grep -lE 'sealed (interface|class) \w*Effect\b' 2>/dev/null)"); noi=$(count "$(printf '%s\n' $PRES | xargs -r grep -lE 'fun onIntent\(' 2>/dev/null)")
nbase=$(count "$(printf '%s\n' $KT | xargs -r grep -lE 'abstract class Store<|interface StoreAction<|class ViewModel<\w+>|fun .*\.updateState\(' 2>/dev/null)")
nuse=$(count "$(printf '%s\n' $PRES | xargs -r grep -lE 'StoreViewModel<|: StoreAction<|Store<\w+>\(|updateState \{' 2>/dev/null)")
if [ "$nin" -gt 0 ] && [ "$noi" -gt 0 ]; then ok "MVI" "Intent ($nin), onIntent ($noi) : entrée unique par ViewModel"
elif [ "$nbase" -gt 0 ] && [ "$nuse" -gt 0 ]; then ok "MVI" "ViewModel<State> (updateState, sendEvent) utilisé par $nuse fichier(s)"
else bad "MVI" "MVI absent : ni Store/StoreAction + updateState, ni XxxIntent/onIntent (Intent: $nin, onIntent: $noi)"; fi
[ "$nef" -gt 0 ] || [ "$nbase" -gt 0 ] && ok "MVI" "événements ponctuels séparés du State" || warn "MVI" "aucun mécanisme d'événements ponctuels (Effect ou sendEvent)"
npv=$(count "$(printf '%s\n' $PRES | xargs -r grep -lE '@Preview' 2>/dev/null)"); [ "$npv" -gt 0 ] && ok "UI" "$npv fichier(s) avec @Preview" || warn "UI" "aucune @Preview trouvée"
var=$(find . -type d -name 'values-*' -not -path '*/build/*' 2>/dev/null | sed 's#.*/##' | sort -u | tr '\n' ' ')
[ -n "$var" ] && ok "Ressources" "variantes de ressources : $var" || bad "Ressources" "aucune variante (values-night, values-en…)"
hard=$(printf '%s\n' $KT | xargs -r grep -nE 'Text\(\s*(text = )?"[^" $]|contentDescription = "[^" $]' 2>/dev/null | grep -v '^\S*:\s*[0-9]*:\s*\*' | head -4)
[ -z "$hard" ] && ok "Ressources" "aucun texte en dur dans les composables" || { warn "Ressources" "texte en dur dans un composable (« utilisation systématique des ressources ») :"; echo "$hard" | cut -c1-110 | sed 's/^/                                /'; }
vm=$(printf '%s\n' $PRES | grep 'ViewModel' | xargs -r grep -nE -- '-> "[^"]{8,}"' 2>/dev/null | head -4)
[ -z "$vm" ] && ok "Ressources" "aucun message utilisateur en dur dans les ViewModel" || { warn "Ressources" "messages en dur dans un ViewModel (à déplacer en strings.xml) :"; echo "$vm" | cut -c1-110 | sed 's/^/                                /'; }
perm=$(printf '%s\n' $MANIFESTS | xargs -r grep -hoE 'android.permission.[A-Z_]+' 2>/dev/null | grep -v INTERNET | sort -u | tr '\n' ' ')
caps=$(( $(count "$(printf '%s\n' $KT | xargs -r grep -lE '(class|interface) \w+Manager' 2>/dev/null)") + ndao ))
[ "$caps" -ge 2 ] && ok "Natives" "capacités natives centralisées (Managers + base locale), permissions : ${perm:-aucune}" || warn "Natives" "moins de 2 capacités natives encapsulées trouvées (permissions : ${perm:-aucune})"
echo "== Fin. Une conclusion se justifie avec des preuves (fichier:ligne), pas avec ce script seul."
