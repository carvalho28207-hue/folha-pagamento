/**
 * Funcionario CLT (regime definido no construtor e imutavel).
 * Os calculos tributarios consultam sempre calcularBruto() e isPJ(),
 * para que as subclasses possam mudar apenas a forma de obter o bruto.
 */
public class Funcionario {

    // ---- Regras fornecidas no enunciado (competencia 2026) ----
    private static final double[] INSS_LIMITES = {1621.00, 2902.84, 4354.27, 8475.55};
    private static final double[] INSS_ALIQUOTAS = {0.075, 0.09, 0.12, 0.14};

    private static final double DESCONTO_SIMPLIFICADO = 607.20;

    // Faixas do IRRF: limite superior da base, aliquota e parcela a deduzir
    private static final double[] IR_LIMITES = {2428.80, 2826.65, 3751.05, 4664.68};
    private static final double[] IR_ALIQUOTAS = {0.0, 0.075, 0.15, 0.225};
    private static final double[] IR_DEDUCOES = {0.0, 182.16, 394.16, 675.49};
    private static final double IR_ALIQUOTA_MAXIMA = 0.275;
    private static final double IR_DEDUCAO_MAXIMA = 908.73;

    private static final double BRUTO_ISENCAO_IR = 5000.00;
    private static final double BRUTO_FIM_REDUCAO_IR = 7350.00;
    private static final double REDUCAO_CONSTANTE = 978.62;
    private static final double REDUCAO_COEFICIENTE = 0.133145;

    private static final double ALIQUOTA_FGTS = 0.08;
    private static final double DAS_MEI = 86.05;

    // ---- Atributos ----
    private String nome;
    private double salarioMensal;
    private final boolean pj;

    // ---- Construtores ----
    public Funcionario(String nome, double salarioMensal) {
        this(nome, salarioMensal, false);
    }

    protected Funcionario(String nome, double salarioMensal, boolean pj) {
        validarNome(nome);
        validarSalario(salarioMensal);
        this.nome = nome;
        this.salarioMensal = salarioMensal;
        this.pj = pj;
    }

    // ---- Validacoes (reaproveitadas pelos setters) ----
    private static void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome deve ser preenchido.");
        }
    }

    private static void validarSalario(double salarioMensal) {
        if (salarioMensal < 0) {
            throw new IllegalArgumentException("O salario mensal nao pode ser negativo.");
        }
    }

    // ---- Acessos ----
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        validarNome(nome);
        this.nome = nome;
    }

    public double getSalarioMensal() {
        return salarioMensal;
    }

    public void setSalarioMensal(double salarioMensal) {
        validarSalario(salarioMensal);
        this.salarioMensal = salarioMensal;
    }

    public boolean isPJ() {
        return pj;
    }

    // ---- Arredondamento ----
    protected static double arredondar(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    // ---- Calculos ----
    public double calcularBruto() {
        return salarioMensal;
    }

    public double calcularINSS() {
        if (isPJ()) {
            return 0.0;
        }
        double bruto = calcularBruto();
        double total = 0.0;
        double inicioDaFaixa = 0.0;
        for (int i = 0; i < INSS_LIMITES.length; i++) {
            if (bruto > inicioDaFaixa) {
                double parcela = Math.min(bruto, INSS_LIMITES[i]) - inicioDaFaixa;
                total += parcela * INSS_ALIQUOTAS[i];
            }
            inicioDaFaixa = INSS_LIMITES[i];
        }
        // Acima do ultimo limite a contribuicao nao aumenta (teto).
        return arredondar(total);
    }

    public double calcularIRRF() {
        if (isPJ()) {
            return 0.0;
        }
        double reducao = calcularReducaoIR();
        double impostoInicial = calcularIRAntesDaReducao();
        return arredondar(Math.max(0.0, impostoInicial - reducao));
    }

    /** IR calculado pela tabela, antes da reducao (arredondado). */
    private double calcularIRAntesDaReducao() {
        double bruto = calcularBruto();
        // Deducao da base: a maior entre INSS e desconto simplificado (nao se somam).
        double deducao = Math.max(calcularINSS(), DESCONTO_SIMPLIFICADO);
        double base = Math.max(0.0, bruto - deducao);

        double aliquota = IR_ALIQUOTA_MAXIMA;
        double parcelaADeduzir = IR_DEDUCAO_MAXIMA;
        for (int i = 0; i < IR_LIMITES.length; i++) {
            if (base <= IR_LIMITES[i]) {
                aliquota = IR_ALIQUOTAS[i];
                parcelaADeduzir = IR_DEDUCOES[i];
                break;
            }
        }
        double imposto = Math.max(0.0, base * aliquota - parcelaADeduzir);
        return arredondar(imposto);
    }

    /** Reducao calculada pela remuneracao bruta (arredondada). */
    private double calcularReducaoIR() {
        double bruto = calcularBruto();
        if (bruto <= BRUTO_ISENCAO_IR) {
            // Ate 5.000 o IRRF final e zero: a reducao cobre todo o imposto inicial.
            return calcularIRAntesDaReducao();
        }
        if (bruto <= BRUTO_FIM_REDUCAO_IR) {
            double reducao = REDUCAO_CONSTANTE - REDUCAO_COEFICIENTE * bruto;
            return arredondar(Math.max(0.0, reducao));
        }
        return 0.0;
    }

    public double calcularFGTS() {
        if (isPJ()) {
            return 0.0;
        }
        return arredondar(calcularBruto() * ALIQUOTA_FGTS);
    }

    public double calcularMEI() {
        return isPJ() ? DAS_MEI : 0.0;
    }

    public double calcularLiquido() {
        // FGTS e deposito do empregador: nao entra nos descontos.
        return arredondar(calcularBruto() - calcularINSS() - calcularIRRF() - calcularMEI());
    }
}
