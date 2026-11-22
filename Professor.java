/**
 * Professor CLT. Reaproveita valor da hora e quantidade de horas de FuncionarioPJ,
 * mas a quantidade representa horas-aula SEMANAIS. O regime (CLT) e definido
 * no construtor, passando pj = false.
 *
 * Observacao de modelagem: herdar de FuncionarioPJ e uma escolha didatica
 * desta atividade (ver reflexao 5 na ficha de testes).
 */
public class Professor extends FuncionarioPJ {

    private static final double SEMANAS_POR_MES = 4.5;
    private static final double DIVISOR_DSR = 6.0;

    public Professor(String nome, double valorHoraAula, double horasSemanais) {
        super(nome, valorHoraAula, horasSemanais, false);
    }

    @Override
    public double calcularBruto() {
        double horasAulaMensais = getHorasTrabalhadas() * SEMANAS_POR_MES;
        double remuneracaoAulas = getValorHora() * horasAulaMensais;
        double dsr = remuneracaoAulas / DIVISOR_DSR;
        return remuneracaoAulas + dsr;
    }
}
