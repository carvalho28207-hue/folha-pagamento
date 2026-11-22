import java.util.Locale;

/**
 * Main: cria os objetos, chama os metodos e apresenta os resultados.
 * Os calculos ficam nas classes Funcionario, FuncionarioPJ e Professor.
 */
public class Main {

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final double TOLERANCIA = 0.001;

    private static int totalVerificacoes = 0;
    private static int falhas = 0;

    public static void main(String[] args) {
        System.out.println("=== PARTE 1: os cinco objetos da secao 6 ===");
        Funcionario ana = new Funcionario("Ana", 3000.00);
        Funcionario bruno = new Funcionario("Bruno", 6000.00);
        FuncionarioPJ carla = new FuncionarioPJ("Carla", 50.00, 120);
        Professor daniel = new Professor("Daniel", 50.00, 20);
        Professor elisa = new Professor("Elisa", 100.00, 20);

        imprimirDemonstrativo(ana);
        imprimirDemonstrativo(bruno);
        imprimirDemonstrativo(carla);
        imprimirDemonstrativo(daniel);
        imprimirDemonstrativo(elisa);

        System.out.println("=== Conferencia com a tabela do enunciado ===");
        conferir("Ana", ana, 3000.00, 248.60, 0.00, 240.00, 0.00, 2751.40);
        conferir("Bruno", bruno, 6000.00, 641.51, 385.10, 480.00, 0.00, 4973.39);
        conferir("Carla", carla, 6000.00, 0.00, 0.00, 0.00, 86.05, 5913.95);
        conferir("Elisa", elisa, 10500.00, 988.09, 1707.05, 840.00, 0.00, 7804.86);
        // Daniel: valores conferidos a mao na ficha de testes
        // (90 h mensais, aulas 4.500,00, DSR 750,00, bruto 5.250,00).
        conferir("Daniel", daniel, 5250.00, 536.51, 89.53, 420.00, 0.00, 4623.96);

        System.out.println();
        System.out.println("=== PARTE 2: testes obrigatorios ===");
        teste1EntradasNegativas();
        teste2CltSalarioZero();
        teste3PjZeroHoras();
        teste4TetoDoInss();
        teste5IrrfIsencaoEReducao();
        teste6LiquidoDuasVezes();
        teste7IndependenciaDosObjetos();

        System.out.println();
        System.out.println("Verificacoes: " + totalVerificacoes + " | Falhas: " + falhas);
    }

    // ---------------- Apresentacao ----------------

    private static String moeda(double valor) {
        return String.format(PT_BR, "%,.2f", valor);
    }

    private static void imprimirDemonstrativo(Funcionario f) {
        System.out.println("Nome:    " + f.getNome());
        System.out.println("Regime:  " + (f.isPJ() ? "PJ (MEI)" : "CLT"));
        System.out.println("Bruto:   R$ " + moeda(f.calcularBruto()));
        System.out.println("INSS:    R$ " + moeda(f.calcularINSS()));
        System.out.println("IRRF:    R$ " + moeda(f.calcularIRRF()));
        System.out.println("FGTS:    R$ " + moeda(f.calcularFGTS()) + " (deposito do empregador)");
        System.out.println("DAS:     R$ " + moeda(f.calcularMEI()));
        System.out.println("Liquido: R$ " + moeda(f.calcularLiquido()));
        System.out.println();
    }

    // ---------------- Verificacoes ----------------

    private static void verificar(String descricao, double esperado, double obtido) {
        totalVerificacoes++;
        boolean ok = Math.abs(esperado - obtido) < TOLERANCIA;
        if (!ok) {
            falhas++;
        }
        System.out.println("  [" + (ok ? "PASSOU" : "FALHOU") + "] " + descricao
                + " | esperado " + moeda(esperado) + " | obtido " + moeda(obtido));
    }

    private static void conferir(String rotulo, Funcionario f, double bruto, double inss,
                                 double irrf, double fgts, double das, double liquido) {
        System.out.println(rotulo + ":");
        verificar("bruto", bruto, f.calcularBruto());
        verificar("INSS", inss, f.calcularINSS());
        verificar("IRRF", irrf, f.calcularIRRF());
        verificar("FGTS", fgts, f.calcularFGTS());
        verificar("DAS", das, f.calcularMEI());
        verificar("liquido", liquido, f.calcularLiquido());
    }

    private static void tentarCriar(String descricao, Runnable criacao) {
        totalVerificacoes++;
        try {
            criacao.run();
            falhas++;
            System.out.println("  [FALHOU] " + descricao + " | o objeto foi criado, mas deveria ser rejeitado");
        } catch (IllegalArgumentException e) {
            System.out.println("  [PASSOU] " + descricao + " | rejeitado: " + e.getMessage());
        }
    }

    // ---------------- Testes obrigatorios ----------------

    private static void teste1EntradasNegativas() {
        System.out.println("Teste 1 - entradas negativas ou nome vazio sao rejeitados:");
        tentarCriar("salario negativo (CLT)", () -> new Funcionario("Zeca", -1.00));
        tentarCriar("valor da hora negativo (PJ)", () -> new FuncionarioPJ("Zeca", -1.00, 10));
        tentarCriar("horas negativas (PJ)", () -> new FuncionarioPJ("Zeca", 50.00, -1));
        tentarCriar("valor da hora-aula negativo (professor)", () -> new Professor("Zeca", -1.00, 10));
        tentarCriar("horas semanais negativas (professor)", () -> new Professor("Zeca", 50.00, -1));
        tentarCriar("nome vazio", () -> new Funcionario("   ", 1000.00));
        tentarCriar("nome nulo", () -> new Funcionario(null, 1000.00));
    }

    private static void teste2CltSalarioZero() {
        System.out.println("Teste 2 - CLT com salario zero: tudo zero:");
        Funcionario f = new Funcionario("Zero", 0.0);
        conferir("Zero", f, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    }

    private static void teste3PjZeroHoras() {
        System.out.println("Teste 3 - PJ com hora 50 e zero horas: DAS continua devido:");
        FuncionarioPJ f = new FuncionarioPJ("Pedro", 50.00, 0);
        conferir("Pedro", f, 0.0, 0.0, 0.0, 0.0, 86.05, -86.05);
    }

    private static void teste4TetoDoInss() {
        System.out.println("Teste 4 - INSS ate o teto (8.475,55) e constante depois dele:");
        Funcionario a = new Funcionario("Salario 8.000", 8000.00);
        Funcionario b = new Funcionario("Salario 10.500", 10500.00);
        Funcionario c = new Funcionario("Salario 12.000", 12000.00);
        verificar("INSS de 8.000", 921.51, a.calcularINSS());
        verificar("INSS de 10.500", 988.09, b.calcularINSS());
        verificar("INSS de 12.000 (igual ao de 10.500)", 988.09, c.calcularINSS());
        System.out.println("  Aumento de 8.000 para 10.500: R$ "
                + moeda(b.calcularINSS() - a.calcularINSS()));
        System.out.println("  Aumento de 10.500 para 12.000: R$ "
                + moeda(c.calcularINSS() - b.calcularINSS()));
    }

    private static void teste5IrrfIsencaoEReducao() {
        System.out.println("Teste 5 - IRRF: isencao em 5.000 e reducao parcial em 6.000:");
        Funcionario a = new Funcionario("Salario 5.000", 5000.00);
        Funcionario b = new Funcionario("Salario 6.000", 6000.00);
        verificar("IRRF de 5.000 (isento pela regra da reducao)", 0.00, a.calcularIRRF());
        verificar("IRRF de 6.000 (imposto 564,85 - reducao 179,75)", 385.10, b.calcularIRRF());
    }

    private static void teste6LiquidoDuasVezes() {
        System.out.println("Teste 6 - calcularLiquido() duas vezes no mesmo objeto:");
        Funcionario f = new Funcionario("Repetido", 6000.00);
        double brutoAntes = f.calcularBruto();
        double primeiro = f.calcularLiquido();
        double segundo = f.calcularLiquido();
        verificar("segunda chamada igual a primeira", primeiro, segundo);
        verificar("bruto nao foi alterado", brutoAntes, f.calcularBruto());
    }

    private static void teste7IndependenciaDosObjetos() {
        System.out.println("Teste 7 - recalcular Ana depois de criar Bruno:");
        Funcionario ana = new Funcionario("Ana", 3000.00);
        double antes = ana.calcularLiquido();
        Funcionario bruno = new Funcionario("Bruno", 6000.00);
        double depois = ana.calcularLiquido();
        verificar("liquido de Ana antes e depois de criar Bruno", antes, depois);
        verificar("liquido de Ana continua 2.751,40", 2751.40, depois);
        verificar("liquido de Bruno e o dele (4.973,39)", 4973.39, bruno.calcularLiquido());
    }
}
