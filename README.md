# BancoDigital

Simulador bancário em Java para o terminal. Abra uma conta corrente e uma poupança, movimente o saldo e confira como cada operação aparece no extrato.

## Executar no VS Code

Use um JDK 17 ou superior. Coloque a pasta `BancoDigital` dentro de uma pasta de projetos e abra **a pasta pai** no VS Code. No terminal integrado, a partir dessa pasta pai, execute:

```sh
javac -encoding UTF-8 -d out BancoDigital/Main.java
java -cp out BancoDigital.Main
```

Se clonou o repositório com `git clone https://github.com/rafael-bernardi/BancoDigital.git`, execute os dois comandos no mesmo diretório em que rodou o clone. Com a extensão Java do VS Code, você também pode abrir `BancoDigital/Main.java` e usar **Run** no método `main`.

## Experimente em dois minutos

1. Abra uma corrente de número `1001` e uma poupança de número `2002`.
2. Deposite R$ 200,00 na corrente e transfira R$ 80,00 para a poupança. A corrente ficará com R$ 117,50, pois o saque usado na transferência tem tarifa de R$ 2,50.
3. Tente sacar R$ 100,00 da poupança. O sistema informa saldo insuficiente e permite continuar usando o menu.
4. Aplique rendimento de 1,5% à poupança. Consulte o extrato para ver o crédito de R$ 1,20 e o saldo de R$ 81,20.
5. Use a opção 7 para gravar `extrato_2002.txt` no diretório de execução. Uma nova exportação da mesma conta atualiza o arquivo.

O menu aceita valores monetários com ponto ou vírgula e até duas casas decimais. A corrente tem R$ 500,00 de cheque especial. O extrato separa a tarifa do valor sacado. A transferência aparece como saque na origem e depósito no destino. As contas e seu histórico ficam em memória durante a sessão. Os extratos exportados permanecem em disco após sair.

## Organização do código

- **Interface** — `Main.java` lê o teclado, apresenta as dez opções e traduz falhas em mensagens para quem usa o terminal.
- **Serviço** — `service/BancoService.java` localiza as contas por número em um `HashMap`, impede duplicidades, coordena transferências e exporta o extrato em UTF-8 com `BufferedWriter` e `FileWriter`.
- **Modelo** — `model/Conta.java` concentra os dados e as operações comuns. Cada conta guarda as transações em um `ArrayList`. Em `model/Transacao.java`, cada lançamento recebe um `LocalDateTime` no momento da operação.
- **Exceção de negócio** — `exception/SaldoInsuficienteException.java` sinaliza quando o saque excede o saldo disponível ou o limite da corrente.

## Os quatro pilares de POO no projeto

**Abstração.** A classe abstrata `Conta` define o contrato de saque e fornece depósito e extrato para qualquer tipo de conta.

**Encapsulamento.** O saldo muda pelos métodos de movimentação. A consulta ao histórico devolve uma cópia imutável da lista, e as transações têm atributos privados e finais.

**Herança.** `ContaCorrente` e `ContaPoupanca` herdam os dados e comportamentos comuns de `Conta`. A poupança acrescenta a aplicação de rendimento.

**Polimorfismo.** Na transferência, o serviço chama `origem.sacar(valor)`. O tipo concreto da conta determina se o saque pode usar o cheque especial e se haverá tarifa.
