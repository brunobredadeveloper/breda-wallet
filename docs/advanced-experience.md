# Roteiro da experiência avançada

Entre no modo local, envie R$ 25,90 para Ana, confira o comprovante, reinicie o app e confira o saldo. Tente um valor superior ao saldo. Entre com outra conta e confira o ledger separado.

## Autenticação e estados

- Inicie sem configuração e confirme identificação de demo local.
- Com Firebase configurado: cadastre conta, saia, teste senha incorreta, entre, recupere senha e envie verificação.
- Reinicie para verificar restauração da sessão.
- Teste sem rede e confira mensagem de erro e liberação do formulário.
- Teste voltar, rotação, texto grande, teclado aberto, carregamento e navegação pelas quatro abas.
- Troque de usuário e confirme ausência dos dados do anterior.

## Limites

O ledger local e a memória de idempotência são salvos atomicamente antes de publicar um débito. Snapshot por conta, Mutex contra concorrência, valores em centavos, replay após reinício e falha de persistência testados. Limite de 1.000 requisições por ledger de demonstração, sem descartar chaves antigas. Metas são um exercício local de planejamento; não movimentam saldo.

Firebase e interação visual aguardam teste manual com configuração pessoal e celular. Não existe certificação para aceitação de cartões, pagamento real ou armazenamento de dados bancários.
