import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.function.DoubleUnaryOperator;
import java.util.function.DoubleBinaryOperator;
import java.util.stream.Collectors;

/**
 * calc - a prefix notation calculator
 */
public class calc {
    public calc() {
        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.print("> ");
            String expr = scanner.nextLine();
            if(expr.equals("quit")) break;
            if(expr.equals("help") || expr.equals("?"))
                usage();
            Queue<String> q = new LinkedList<>();
            Scanner exprScanner = new Scanner(expr);
            while(exprScanner.hasNext())
                q.add(exprScanner.next());
            System.out.println("Tokens: " + q);
            try {
                Expression res = parse(q);
                System.out.println("In-fix: " + res);
                System.out.println(res.eval());
            } catch(Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private Expression parse(Queue<String> q) throws Exception {
        if(q.isEmpty()) throw new Exception("Invalid input.");
        String token = q.remove().replace(")","");
        try {
            return new Number(Double.parseDouble(token));
        } catch(NumberFormatException nfe) {
            switch(token) {
                case "(sqrt": return new SquareRoot(parse(q));
                case "(+": return new Add(parse(q), parse(q));
                case "(*": return new Multiply(parse(q), parse(q));
                case "(/": return new Divide(parse(q), parse(q));
                case "(-":
                    if(q.peek().contains(")"))
                        return new Negate(parse(q));
                    else
                        return new Subtract(parse(q), parse(q));
            }
        }
        throw new Exception("Invalid input.");
    }

    public static void main(String[] args) throws Exception {
        try {
            new calc();
        } catch(Exception e) {
            System.exit(1);
        }
    }

    private static void usage() {
        System.out.println("calc - a prefix notation calculator");
        System.out.println("Enter an expression below to be evaluated.");
    }

    public interface Expression {
        public double eval();
        public String toString();
    }

    static class Number implements Expression {
        double val;

        Number(double val) {
            this.val = val;
        }

        public double eval() {
            return val;
        }

        public String toString() {
            return ""+val;
        }
    }

    static class UnaryExpression implements Expression {
        private String operator;
        private double defaultValue;
        private DoubleUnaryOperator op;
        private Expression expression;

        UnaryExpression(String operator, double defaultValue,
                        DoubleUnaryOperator op, Expression expression) {
            this.operator = operator;
            this.defaultValue = defaultValue;
            this.op = op;
            this.expression = expression;
        }

        public double eval() {
            return op.applyAsDouble(expression.eval());
        }

        public String toString() {
            return operator + "(" + expression.toString() + ")";
        }
    }

    static class BinaryExpression implements Expression {
        private String operator;
        private double defaultValue;
        private DoubleBinaryOperator op;
        private Queue<Expression> expressions;

        BinaryExpression(String operator, double defaultValue,
                         DoubleBinaryOperator op, Expression[] expressions) {
            this.operator = operator;
            this.defaultValue = defaultValue;
            this.op = op;
            this.expressions = new LinkedList<Expression>();
            for(Expression expression : expressions)
                this.expressions.add(expression);
        }

        public double eval() {
            return expressions.stream()
                              .mapToDouble(Expression::eval)
                              .reduce(op)
                              .orElse(defaultValue);
        }

        public String toString() {
            return expressions.stream()
                              .map(Object::toString)
                              .collect(Collectors.joining(" " + operator + " ", "(", ")"));
        }
    }

    static class Negate extends UnaryExpression {
        Negate(Expression expression) {
            super("-", 0.0, (v) -> -v, expression);
        }
    }

    static class SquareRoot extends UnaryExpression {
        SquareRoot(Expression expression) {
            super("√", 0.0, (v) -> Math.sqrt(v), expression);
        }
    }

    static class Add extends BinaryExpression {
        Add(Expression... expressions) {
            super("+", 0.0, (l, r) -> l + r, expressions);
        }
    }

    static class Subtract extends BinaryExpression {
        Subtract(Expression... expressions) {
            super("-", 0.0, (l, r) -> l - r, expressions);
        }
    }

    static class Multiply extends BinaryExpression {
        Multiply(Expression... expressions) {
            super("*", 0.0, (l, r) -> l * r, expressions);
        }
    }

    static class Divide extends BinaryExpression {
        Divide(Expression... expressions) {
            super("/", 0.0, (l, r) -> l / r, expressions);
        }
    }
}
