import java.util.Queue;
import java.util.LinkedList;
import java.util.StringTokenizer;

public class calc {
    public calc(Queue<String> q) {
        expr res = parse(q);
        System.out.println(res);
        System.out.println(res.eval());
    }

    private expr parse(Queue<String> q) {
        String token = q.remove();
        try {
            return new num(Integer.parseInt(token));
        } catch(NumberFormatException nfe) {
            if(token.equals("(+")) {
                return new add(parse(q),parse(q));
            } else if(token.equals("(-")) {
                return new sub(parse(q),parse(q));
            } else if(token.equals("(*")) {
                return new mul(parse(q),parse(q));
            } else if(token.equals("(/")) {
                return new div(parse(q),parse(q));
            } else if(token.equals("(sqrt")) {
                return new sqrt(parse(q));
            }
        }
        System.out.println("Invalid input.");
        System.exit(1);
        return null;
    }

    public static void main(String[] args) {
        if(args.length != 1) usage();
        String expr = args[0];
        Queue<String> q = new LinkedList<>();
        StringTokenizer st = new StringTokenizer(expr);
        while(st.hasMoreTokens()) {
            q.add(st.nextToken(" )"));
        }
        new calc(q);
    }

    private static void usage() {
        System.out.println("usage: calc <expr>");
        System.exit(1);
    }

    private interface expr {
        public double eval();
    }

    private class num implements expr {
        double val;

        num(double val) {
            this.val = val;
        }

        public double eval() {
            return val;
        }

        public String toString() {
            return ""+val;
        }
    }

    private class add implements expr {
        expr left;
        expr right;

        add(expr left, expr right) {
            this.left = left;
            this.right = right;
        }

        public double eval() {
            return left.eval() + right.eval();
        }

        public String toString() {
            return "(" + left.toString() + " + " + right.toString() + ")";
        }
    }

    private class sub implements expr {
        expr left;
        expr right;

        sub(expr left, expr right) {
            this.left = left;
            this.right = right;
        }

        public double eval() {
            return left.eval() - right.eval();
        }

        public String toString() {
            return "(" + left.toString() + " - " + right.toString() + ")";
        }
    }

    private class mul implements expr {
        expr left;
        expr right;

        mul(expr left, expr right) {
            this.left = left;
            this.right = right;
        }

        public double eval() {
            return left.eval() * right.eval();
        }

        public String toString() {
            return "(" + left.toString() + " * " + right.toString() + ")";
        }
    }

    private class div implements expr {
        expr left;
        expr right;

        div(expr left, expr right) {
            this.left = left;
            this.right = right;
        }

        public double eval() {
            return left.eval() - right.eval();
        }

        public String toString() {
            return "(" + left.toString() + " / " + right.toString() + ")";
        }
    }

    private class sqrt implements expr {
        expr operand;

        sqrt(expr operand) {
            this.operand = operand;
        }

        public double eval() {
            return Math.sqrt(operand.eval());
        }

        public String toString() {
            return "sqrt(" + operand.toString() + ")";
        }
    }
}
