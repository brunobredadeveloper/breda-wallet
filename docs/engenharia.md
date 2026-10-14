# Engenharia — Breda Wallet

## Problema técnico

Explorar uma jornada de carteira digital e demonstrar como proteger saldo e histórico diante de repetição de comandos e falhas de persistência.

## Responsabilidades e código

| Fronteira | Responsabilidade real | Onde inspecionar |
|---|---|---|
| Domínio | Money, Transfer e WalletRepository | [Wallet.kt](../domain/src/main/kotlin/dev/breda/wallet/domain/Wallet.kt) |
| Caso de uso / apresentação | TransferMoney e WalletViewModel | [TransferMoney.kt](../app/src/main/kotlin/dev/breda/wallet/TransferMoney.kt) |
| Persistência | DemoWalletRepository e armazenamento local | [DemoWalletRepository.kt](../data/src/main/kotlin/dev/breda/wallet/data/DemoWalletRepository.kt) |
| Composição e autenticação | Factory e adapters de conta/Firebase | [ViewModelInjection.kt](../app/src/main/kotlin/dev/breda/wallet/ViewModelInjection.kt) |

## Invariantes e decisões

- Repetição da mesma chave não deve duplicar o débito.
- Falha de gravação não pode publicar novo saldo.
- Planejamento de metas não movimenta dinheiro.

## Como examinar os testes

Os arquivos abaixo fazem parte do código versionado. A existência de uma suíte não comprova sua aprovação; o resultado remoto está em [evidências](evidencias.md).

- [app/src/androidTest/kotlin/dev/breda/wallet/ExperienceJourneyTest.kt](../app/src/androidTest/kotlin/dev/breda/wallet/ExperienceJourneyTest.kt)
- [app/src/test/kotlin/dev/breda/wallet/ArchitectureContractTest.kt](../app/src/test/kotlin/dev/breda/wallet/ArchitectureContractTest.kt)
- [app/src/test/kotlin/dev/breda/wallet/SessionIsolationTest.kt](../app/src/test/kotlin/dev/breda/wallet/SessionIsolationTest.kt)
- [app/src/test/kotlin/dev/breda/wallet/TransferMoneyTest.kt](../app/src/test/kotlin/dev/breda/wallet/TransferMoneyTest.kt)
- [data/src/test/kotlin/dev/breda/wallet/data/DemoWalletRepositoryTest.kt](../data/src/test/kotlin/dev/breda/wallet/data/DemoWalletRepositoryTest.kt)
- [data/src/test/kotlin/dev/breda/wallet/data/WalletPersistenceTest.kt](../data/src/test/kotlin/dev/breda/wallet/data/WalletPersistenceTest.kt)

## Limites e compromissos

Dinheiro fictício; sem conta bancária, Pix real ou integração com adquirente. Hash de UID organiza os arquivos, mas não os criptografa. O ledger de demonstração tem limite de 1.000 requisições, sem descarte de chaves antigas.

A separação de responsabilidades documentada é a encontrada no código. Não se afirma certificação SOLID, paridade com produtos comerciais, auditoria de segurança ou operação em produção.

## Reproduzir a validação

```sh
./gradlew :domain:test :data:test :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

[Voltar ao README](../README.md) · [Fluxos](fluxos.md)
