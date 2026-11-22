/**
 * Prestador PJ (MEI): bruto = valor da hora x horas trabalhadas no mes.
 * O regime e definido no construtor; a tributacao continua centralizada em Funcionario.
 */
public class FuncionarioPJ extends Funcionario {

    private double valorHora;
    private double horasTrabalhadas;

    public FuncionarioPJ(String nome, double valorHora, double horasTrabalhadas) {
        this(nome, valorHora, horasTrabalhadas, true);
    }

    protected FuncionarioPJ(String nome, double valorHora,
                            double horasTrabalhadas, boolean pj) {
        super(nome, 0.0, pj);
        validarValorHora(valorHora);
        validarHoras(horasTrabalhadas);
        this.valorHora = valorHora;
        this.horasTrabalhadas = horasTrabalhadas;
    }

    private static void validarValorHora(double valorHora) {
        if (valorHora < 0) {
            throw new IllegalArgumentException("O valor da hora nao pode ser negativo.");
        }
    }

    private static void validarHoras(double horas) {
        if (horas < 0) {
            throw new IllegalArgumentException("A quantidade de horas nao pode ser negativa.");
        }
    }

    public double getValorHora() {
        return valorHora;
    }

    public void setValorHora(double valorHora) {
        validarValorHora(valorHora);
        this.valorHora = valorHora;
    }

    public double getHorasTrabalhadas() {
        return horasTrabalhadas;
    }

    public void setHorasTrabalhadas(double horasTrabalhadas) {
        validarHoras(horasTrabalhadas);
        this.horasTrabalhadas = horasTrabalhadas;
    }

    @Override
    public double calcularBruto() {
        return valorHora * horasTrabalhadas;
    }
}
