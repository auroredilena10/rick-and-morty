# Format du rapport d'audit

Court, classé par gain. Chaque ligne cite l'exigence concernée et une preuve vérifiable.

```markdown
## Audit du <date> — <projet> (commit <hash>)

### Bilan
| Domaine | Conformes | Partiels | Manquants |
|---|---|---|---|
| Architecture | n | n | n |
| Qualité du code | n | n | n |
| Front-end | n | n | n |

### Écarts, du plus rentable au moins rentable
| # | Exigence | Écart (preuve) | Correction proposée | Effort |
|---|---|---|---|---|
| 1 | KDoc avec balises sur les composants complexes | 25 fichiers sur 172 en ont (`data/…/XxxRepositoryImpl.kt`) | Ajouter `@param` / `@return` | moyen |

### Déjà conforme (preuves)
- Versions centralisées : aucun numéro dans un `build.gradle.kts` (`gradle.properties` ou catalogue).

### À préparer à l'oral
- Expliquer le cycle MVI : action → ViewModel (`updateState`) → State → écran, et le rôle des événements ponctuels (`sendEvent`).

### Non vérifié
- Ce que je n'ai pas pu contrôler (ex. comportement dans l'émulateur).
```

Règles : jamais « conforme » sans preuve ; un point partiel reste un écart ; dire clairement ce qui n'a pas été vérifié ; jamais « conforme » tant que le build affiche une seule erreur ou un seul avertissement (citer la sortie du build comme preuve).
