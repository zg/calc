import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.function.DoubleUnaryOperator;
import java.util.function.DoubleBinaryOperator;
import java.util.stream.Collectors;

/**
 * calc - an infix notation calculator
 */
public class calc {
    static final String SEPARATOR = ",";
    static final String OPERATORS = "-+*/^%";
    static final String FUNCTIONS = "sin cos tan asin acos atan exp min max";

    private static boolean debug = false;

    public calc(boolean debug) throws Exception {
        this.debug = debug;
        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.print("> ");
            String input = scanner.nextLine();
            if(input.equals("quit")) break;
            if(input.equals("help") || input.equals("?"))
                usage();

            Stack<String> rpn = parse(input);
            if (debug) System.out.println("RPN: " + rpn);

            Expression ast = toAST(rpn);
            if (debug) System.out.println("AST: " + ast);

            double val = ast.eval();
            System.out.println(val);
        }
    }

    private Stack<String> parse(String input) throws Exception {
        Stack<String> output = new Stack<String>();
        Stack<String> stackOps = new Stack<String>();
        input = input.replace(" ", "")
                .replace("°", "*" + Double.toString(Math.PI) + "/180")
                .replace("(-", "(0-")
                .replace(",-", ",0-")
                .replace("(+", "(0+")
                .replace(",+", ",0+")
                .replace("pi", Double.toString(Math.PI))
                .replace("π", Double.toString(Math.PI))
                .replace("e", Double.toString(Math.E))
                .toLowerCase();
        if(input.charAt(0) == '-' || input.charAt(0) == '+')
            input = "0" + input;

        StringTokenizer stringTokenizer = new StringTokenizer(input,
                OPERATORS + SEPARATOR + "()", true);

        while(stringTokenizer.hasMoreTokens()) {
            String token = stringTokenizer.nextToken();
            if(isSeparator(token)) {
                while(!stackOps.empty() &&
                      !isOpenBracket(stackOps.lastElement())) {
                    output.push(stackOps.pop());
                }
            } else if(isOpenBracket(token)) {
                stackOps.push(token);
            } else if(isCloseBracket(token)) {
                while(!stackOps.empty()
                   && !isOpenBracket(stackOps.lastElement()))
                    output.push(stackOps.pop());
                stackOps.pop();
                if(!stackOps.empty() && isFunction(stackOps.lastElement()))
                    output.push(stackOps.pop());
            } else if(isNumber(token)) {
                output.push(token);
            } else if(isOperator(token)) {
                while(!stackOps.empty()
                        && isOperator(stackOps.lastElement())
                        && precedence(token) <= precedence(stackOps
                                .lastElement())) {
                    output.push(stackOps.pop());
                }
                stackOps.push(token);
            } else if(isFunction(token)) {
                stackOps.push(token);
            } else {
                throw new Exception("Unrecognized token");
            }
        }
        while(!stackOps.empty()) {
            output.push(stackOps.pop());
        }

        Collections.reverse(output);
        return output;
    }

    private static Expression toAST(Stack<String> input) {
        Stack<Expression> stack = new Stack<Expression>();
        while(!input.isEmpty()) {
            String top = input.pop();
            if(isNumber(top)) {
                stack.push(new Number(Double.parseDouble(top)));
            } else {
                Expression e1 = stack.pop();
                switch(top) {
                    case "+":
                        stack.push(new Add(stack.pop(), e1));
                    break;
                    case "-":
                        stack.push(new Subtract(stack.pop(), e1));
                    break;
                    case "*":
                        stack.push(new Multiply(stack.pop(), e1));
                    break;
                    case "/":
                        stack.push(new Divide(stack.pop(), e1));
                    break;
                    case "^":
                        stack.push(new Power(stack.pop(), e1));
                    break;
                    case "%":
                        stack.push(new Modulo(stack.pop(), e1));
                    break;
                    case "min":
                        stack.push(new Minimum(stack.pop(), e1));
                    break;
                    case "max":
                        stack.push(new Maximum(stack.pop(), e1));
                    break;
                    case "sin":
                        stack.push(new Sine(e1));
                    break;
                    case "cos":
                        stack.push(new Cosine(e1));
                    break;
                    case "tan":
                        stack.push(new Tangent(e1));
                    break;
                    case "asin":
                        stack.push(new Arcsine(e1));
                    break;
                    case "acos":
                        stack.push(new Arccosine(e1));
                    break;
                    case "atan":
                        stack.push(new Arctangent(e1));
                    break;
                    case "exp":
                        stack.push(new Exponent(e1));
                    break;
                }
            }
        }
        return stack.pop();
    }

    private static boolean isFunction(String func) {
        return FUNCTIONS.indexOf(func) > -1;
    }

    private static int precedence(String op) {
        String[] opSplit = OPERATORS.split(" ");
        for(int i = 0; i < opSplit.length; i++)
            if(opSplit[i].equals(op))
                return i;
        return Integer.MAX_VALUE;
    }

    private static boolean isOperator(String op) {
        for(int i = 0; i < OPERATORS.length(); i++)
            if(OPERATORS.charAt(i) == op.charAt(0))
                return true;
        return false;
    }

    private static boolean isNumber(String token) {
        try {
            double d = Double.parseDouble(token);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    private static boolean isSeparator(String token) {
        return token.equals(",");
    }

    private static boolean isOpenBracket(String token) {
        return token.equals("(");
    }

    private static boolean isCloseBracket(String token) {
        return token.equals(")");
    }

    public static void main(String[] args) throws Exception {
        boolean debug = false;
        if(args.length > 0 && (args[0].equals("-v") || args[0].equals("--verbose")))
            debug = true;
        try {
            new calc(debug);
        } catch(Exception e) {
            System.exit(1);
        }
    }

    private static void usage() {
        System.out.println("calc - an infix notation calculator");
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

    static class Sine extends UnaryExpression {
        Sine(Expression expression) {
            super("sin", 0.0, (v) -> Math.sin(v), expression);
        }
    }

    static class Cosine extends UnaryExpression {
        Cosine(Expression expression) {
            super("cos", 0.0, (v) -> Math.cos(v), expression);
        }
    }

    static class Tangent extends UnaryExpression {
        Tangent(Expression expression) {
            super("tan", 0.0, (v) -> Math.tan(v), expression);
        }
    }

    static class Arcsine extends UnaryExpression {
        Arcsine(Expression expression) {
            super("asin", 0.0, (v) -> Math.asin(v), expression);
        }
    }

    static class Arccosine extends UnaryExpression {
        Arccosine(Expression expression) {
            super("acos", 0.0, (v) -> Math.acos(v), expression);
        }
    }

    static class Arctangent extends UnaryExpression {
        Arctangent(Expression expression) {
            super("atan", 0.0, (v) -> Math.atan(v), expression);
        }
    }

    static class Exponent extends UnaryExpression {
        Exponent(Expression expression) {
            super("exp", 0.0, (v) -> Math.exp(v), expression);
        }
    }

    static class Minimum extends BinaryExpression {
        Minimum(Expression... expressions) {
            super("min", 0.0, (l, r) -> Math.min(l, r), expressions);
        }
    }

    static class Maximum extends BinaryExpression {
        Maximum(Expression... expressions) {
            super("max", 0.0, (l, r) -> Math.max(l, r), expressions);
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

    static class Power extends BinaryExpression {
        Power(Expression... expressions) {
            super("^", 0.0, (l, r) -> Math.pow(l, r), expressions);
        }
    }

    static class Modulo extends BinaryExpression {
        Modulo(Expression... expressions) {
            super("%", 0.0, (l, r) -> l % r, expressions);
        }
    }
}
