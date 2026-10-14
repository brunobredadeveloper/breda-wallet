# Evidências e distribuição — Breda Wallet

Consulta realizada em **14/10/2026**. Esta página separa o que existe no código, o que foi executado e o que ainda precisa de validação física.

## Execução consultada

- [GitHub Actions #38172912981](https://github.com/brunobredadeveloper/breda-wallet/actions/runs/38172912981)
- Revisão executada: `12787667c261b5f63a184745f7885ccf6f4bfbb6`.
- Workflow: **Tests and APK**.
- Conclusão retornada pelo GitHub: **success**.

Alterações documentais posteriores não equivalem a uma nova compilação do aplicativo.

## Artefatos encontrados

- `breda-wallet-reports` — ID `11714587480`; disponível na consulta; sujeito à expiração pelo GitHub.
- `breda-wallet-ux-evidence` — ID `11714557869`; disponível na consulta; sujeito à expiração pelo GitHub.
- `breda-wallet-debug-apk` — ID `11714422680`; disponível na consulta; sujeito à expiração pelo GitHub.

Para baixar: abra a execução, vá a **Artifacts**, baixe o ZIP e extraia o conteúdo. O GitHub pode exigir login. APK é para Android; bundle JavaScript, relatório e ZIP de fontes não são instaladores. Builds iOS de simulador ou sem assinatura não instalam diretamente no iPhone.

## Capturas incorporadas

- [03-home.png](imagens/03-home.png) — artefato de testes de interface do aplicativo.
- [05-success.png](imagens/05-success.png) — artefato de testes de interface do aplicativo.
- [07-history.png](imagens/07-history.png) — artefato de testes de interface do aplicativo.

Origem: https://github.com/brunobredadeveloper/breda-wallet/actions/runs/38172912981. Os dados exibidos são de demonstração. As capturas foram mantidas sem retoques.

## Avaliação física pendente

Testes automatizados e compilação não substituem avaliação de acessibilidade, interrupções, recursos do aparelho e instalação/distribuição pelo titular. Dinheiro fictício; sem conta bancária, Pix real ou integração com adquirente. Hash de UID organiza os arquivos, mas não os criptografa. O ledger de demonstração tem limite de 1.000 requisições, sem descarte de chaves antigas.

[Fluxos e checklist](fluxos.md) · [README](../README.md)
