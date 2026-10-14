# Arquitetura e decisões — Breda Wallet

A interface Compose observa estado e envia eventos. As regras e a integração são separadas por contratos recebidos por construtor. A composição ocorre em `ViewModelInjection.kt`; o ViewModel não busca dependências em um Service Locator.

## Fronteiras do projeto

`TransferMoney` valida destinatário/valor e usa `WalletRepository`. O adapter de dados conserva saldo e idempotência na mesma gravação; o ViewModel publica a fase de processamento. Metas são planejamento local sem efeito sobre o saldo.

## Autenticação e sessão

`AccountSession` depende de `Authentication` e `SessionStore`. Adapters traduzem Firebase e preferências; a sessão seleciona o namespace da conta antes de publicar seus dados. O modo local é explícito, não um login Firebase. Senhas não são persistidas. Hash do UID não é criptografia do conteúdo.

## SOLID aplicado com limites claros

- Responsabilidade única: UI apresenta, ViewModel coordena e adapters executam IO.
- Inversão de dependência: contratos pequenos entram por construtor.
- Substituição: testes usam fakes dos contratos e verificam os resultados esperados.
- Extensão: gateways/repositórios podem variar por implementações dos ports; não se afirma ausência de mudanças na composição.
- Segregação: autenticação, sessão e recursos de negócio possuem fronteiras específicas.

## Padrões e evidências

Repository/Adapter organizam dados e APIs externas; Factory monta dependências; StateFlow/Observer publica mudanças. As regras e os testes específicos deste repositório estão em [engenharia](engenharia.md). Outbox e versionamento pertencem ao Field Orders; não são atribuídos indiscriminadamente aos outros apps.

Dinheiro fictício; sem conta bancária, Pix real ou integração com adquirente. Hash de UID organiza os arquivos, mas não os criptografa. O ledger de demonstração tem limite de 1.000 requisições, sem descarte de chaves antigas.

[Fluxos](fluxos.md) · [Evidências remotas](evidencias.md)
