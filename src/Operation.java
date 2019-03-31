public class Operation {
    private Double operand1;
    private Double operand2;
    private String operation;
    private Double result = null;

    public Operation(Double operand1, Double operand2, String operation) {
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.operation = operation;
    }

    public void compute() {
        switch (operation) {
            case "+":
                result = operand1 + operand2;
                break;
            case "-":
                result = operand1 - operand2;
                break;
            case "*":
                result = operand1 * operand2;
                break;
            case "/":
                result = operand1 / operand2;
                break;
        }
    }

    public Double getResult() {
        return result;
    }
}
