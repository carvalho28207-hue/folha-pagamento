# Estudo guiado avaliativo — Folha de pagamento em Java

**Professor:** Alexandre Montanha
**Estudante:** ______________________ **RA:** ______________ **Turma:** ______________
**Data:** 10/11/2026

## Declaração de uso de IA

Informo, de forma intencional e transparente, que usei o Claude (Anthropic) como ferramenta de apoio neste trabalho.

- **O que fiz eu mesmo:** configurei o ambiente (Java, VS Code, Git) e o repositório no GitHub, escrevi a estrutura inicial da classe `Funcionario` (atributos, os dois construtores com validação e `isPJ()`) e estudei, em conversa guiada com o Claude, os conceitos de classe, objeto, construtor, `this` e `protected`.
- **O que foi escrito pelo Claude, a meu pedido:** os cálculos de `Funcionario`, as classes `FuncionarioPJ` e `Professor`, a `Main`, esta ficha de testes e o rascunho das respostas de reflexão.
- Acompanhei e supervisionei o processo e tentei avançar por conta própria até onde consegui. Registro isso por escolha própria, para que a avaliação considere a real autoria de cada parte.

Os valores da coluna "Resultado obtido" vêm de execuções reais do programa (Java 21, `javac *.java` e `java Main`). A saída completa está em `evidencia_execucao.txt`.

## 1. Os cinco objetos da seção 6

| Objeto e entrada | Bruto | INSS | IRRF | FGTS | DAS | Líquido |
|---|---:|---:|---:|---:|---:|---:|
| Ana: CLT, mensal 3.000 | 3.000,00 | 248,60 | 0,00 | 240,00 | 0,00 | 2.751,40 |
| Bruno: CLT, mensal 6.000 | 6.000,00 | 641,51 | 385,10 | 480,00 | 0,00 | 4.973,39 |
| Carla: PJ, hora 50, 120 h/mês | 6.000,00 | 0,00 | 0,00 | 0,00 | 86,05 | 5.913,95 |
| Daniel: professor, hora-aula 50, 20 h/semana | 5.250,00 | 536,51 | 89,53 | 420,00 | 0,00 | 4.623,96 |
| Elisa: professor, hora-aula 100, 20 h/semana | 10.500,00 | 988,09 | 1.707,05 | 840,00 | 0,00 | 7.804,86 |

Ana, Bruno, Carla e Elisa coincidem com os valores do enunciado em todas as colunas. Os de Daniel foram calculados abaixo.

### Cálculo manual de Daniel

1. Horas-aula mensais = 20 × 4,5 = **90**.
2. Remuneração das aulas = 50 × 90 = **4.500,00**.
3. DSR didático = 4.500,00 ÷ 6 = **750,00**.
4. Bruto = 4.500,00 + 750,00 = **5.250,00**.
5. INSS por faixa:
   - 1.621,00 × 7,5% = 121,575
   - 1.281,84 × 9% = 115,3656
   - 1.451,43 × 12% = 174,1716
   - 895,73 × 14% = 125,4022
   - soma = 536,5144, arredondado para **536,51**
6. IRRF:
   - dedução = maior entre INSS (536,51) e desconto simplificado (607,20) = **607,20**
   - base = 5.250,00 − 607,20 = 4.642,80, que cai na faixa de 22,5% (parcela 675,49)
   - imposto inicial = 4.642,80 × 22,5% − 675,49 = **369,14**
   - redução = 978,62 − 0,133145 × 5.250 = 279,60875, arredondado para **279,61**
   - IRRF final = 369,14 − 279,61 = **89,53**
7. FGTS = 8% × 5.250,00 = **420,00** (não é subtraído).
8. Líquido = 5.250,00 − 536,51 − 89,53 = **4.623,96**.

## 2. Ficha de testes

| Teste | Resultado esperado | Resultado obtido | Passou? | Correção realizada |
|---|---|---|---|---|
| Conferência de Daniel (cálculo manual × programa) | INSS 536,51; IRRF 89,53; líquido 4.623,96 | Programa: 536,51; 89,53; 4.623,96 | Sim | Na primeira conta manual, os valores esperados de Daniel estavam errados (INSS 536,50 e IRRF 107,87). Erro 1: multiplicação 895,73 × 14% feita errada. Erro 2: usei o INSS como dedução, mas o INSS (536,51) é menor que o desconto simplificado (607,20), que é a dedução correta. Os valores esperados foram refeitos e conferidos por um cálculo independente; o código não precisou mudar. |
| 1. Entradas negativas | Criação rejeitada com `IllegalArgumentException` | 7 de 7 rejeitadas, com mensagem explicativa (salário, valor da hora, horas, hora-aula, horas semanais negativos; nome vazio; nome nulo) | Sim | Nenhuma |
| 2. CLT com salário 0 | Todos os cálculos zero | Bruto, INSS, IRRF, FGTS, DAS e líquido = 0,00 | Sim | Nenhuma |
| 3. PJ, hora 50, 0 horas | Bruto 0,00; DAS 86,05; líquido −86,05 | Bruto 0,00; DAS 86,05; líquido −86,05 | Sim | Nenhuma |
| 4. Teto do INSS | 8.000 → 921,51; 10.500 → 988,09; 12.000 → 988,09 | 921,51; 988,09; 988,09 (aumento de 66,58 entre 8.000 e 10.500; 0,00 entre 10.500 e 12.000) | Sim | Nenhuma |
| 5. IRRF em 5.000 e 6.000 | 5.000 → 0,00 (isenção); 6.000 → 385,10 (redução parcial) | 0,00; 385,10 | Sim | Nenhuma |
| 6. `calcularLiquido()` duas vezes | Mesmo resultado nas duas chamadas; bruto inalterado | 4.973,39 nas duas; bruto 6.000,00 antes e depois | Sim | Nenhuma |
| 7. Ana depois de criar Bruno | Valores de Ana inalterados | Líquido de Ana 2.751,40 antes e depois; Bruno 4.973,39 | Sim | Nenhuma |

### Motivo de cada resultado

1. Os construtores validam antes de atribuir. Se algum valor é inválido, lançam `IllegalArgumentException` e o objeto não chega a existir.
2. Com bruto 0, nenhuma faixa de INSS é alcançada, a base do IR é 0, o imposto é 0 e o FGTS (8% de 0) é 0. Como é CLT, o DAS também é 0.
3. O PJ tem bruto = valor da hora × horas = 0. O DAS é fixo e continua devido, então o líquido é 0 − 86,05 = −86,05.
4. O INSS é progressivo por faixa. Acima de 8.475,55 a contribuição não cresce, então 10.500 e 12.000 dão o mesmo valor (988,09, o teto).
5. Com bruto até 5.000, a redução cobre todo o imposto e o IRRF final é zero. Com 6.000 a redução é parcial (978,62 − 0,133145 × 6.000 = 179,75): imposto inicial 564,85 − 179,75 = 385,10.
6. Os métodos de cálculo só leem atributos e devolvem valores. Não alteram o objeto, então chamar duas vezes dá o mesmo resultado.
7. Cada objeto tem os próprios atributos. Os cálculos não usam estado compartilhado, então criar Bruno não muda Ana.

## 3. Reflexão

**1. Qual é a diferença entre classe e objeto? Identifique dois objetos do seu programa.**
A classe é o molde: define quais dados e quais métodos um tipo de coisa tem (`Funcionario` define nome, salário, regime e os cálculos). O objeto é uma instância concreta criada a partir desse molde com `new`, e cada um guarda seus próprios valores. Dois objetos do programa: `ana` (`new Funcionario("Ana", 3000.00)`) e `bruno` (`new Funcionario("Bruno", 6000.00)`). Os dois vêm da mesma classe, mas têm salários e resultados diferentes.

**2. O que o construtor garante na criação? Por que o regime não tem setter?**
O construtor garante que o objeto já nasce válido: nome preenchido e valores não negativos, senão a criação é rejeitada com exceção. Ele também define o regime, uma única vez. O regime não tem setter porque é `final` e decide toda a tributação (INSS, IRRF, FGTS, DAS). Se pudesse mudar depois, um objeto poderia passar de CLT para PJ no meio dos cálculos e dar resultados inconsistentes.

**3. Por que o FGTS aparece no demonstrativo, mas não reduz o líquido?**
O FGTS é um depósito feito pelo empregador em uma conta do trabalhador, somado ao salário e não descontado dele. Aparece no demonstrativo por informação, mas o líquido é apenas bruto − INSS − IRRF (− DAS no caso de PJ).

**4. Qual método foi sobrescrito? O que acontece quando o cálculo de INSS chama esse método em um professor?**
`calcularBruto()` foi sobrescrito em `FuncionarioPJ` (valor da hora × horas) e de novo em `Professor` (horas semanais × 4,5, mais o DSR). Quando `calcularINSS()`, que está em `Funcionario`, chama `calcularBruto()` em um professor, o Java executa a versão do `Professor`, porque o método chamado depende do tipo real do objeto (polimorfismo). Por isso o INSS de Daniel é calculado sobre 5.250,00 e não sobre o `salarioMensal` herdado, que vale 0.

**5. Por que um professor CLT herdar de `FuncionarioPJ` causa um problema de modelagem? Sugira um nome ou uma organização melhor.**
Herança expressa "é um": um professor CLT não é um prestador PJ. A hierarquia só funciona porque o atributo `pj` é desligado na criação, ou seja, o `Professor` herda algo e depois precisa se desfazer dessa identidade. Isso confunde o código (um objeto `FuncionarioPJ` que não é PJ) e pode causar erros se alguém checar o tipo da classe. Uma organização melhor é criar uma classe intermediária, por exemplo `FuncionarioPorHora` (ou `FuncionarioHorista`), que herda de `Funcionario` e guarda `valorHora` e `horas`. `FuncionarioPJ` e `Professor` herdariam dela, e cada uma sobrescreveria `calcularBruto()`. O cálculo por hora fica compartilhado e cada classe tem o nome que condiz com o que ela é.
