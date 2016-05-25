import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.StringTokenizer;

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
            StringTokenizer st = new StringTokenizer(expr);
            while(st.hasMoreTokens()) {
                q.add(st.nextToken(" "));
            }
            System.out.println(q);
            try {
                expr res = parse(q);
                System.out.println(res.eval());
            } catch(Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private expr parse(Queue<String> q) throws Exception {
        if(q.size() == 0) throw new Exception("Invalid input.");
        String token = q.remove().replace(")","");
        try {
            return new num(Double.parseDouble(token));
        } catch(NumberFormatException nfe) {
            if(token.equals("(+")) {
                return new add(parse(q), parse(q));
            } else if(token.equals("(-")) {
                if(q.peek().contains(")"))
                    return new neg(parse(q));
                else
                    return new sub(parse(q), parse(q));
            } else if(token.equals("(*")) {
                return new mul(parse(q),parse(q));
            } else if(token.equals("(/")) {
                return new div(parse(q),parse(q));
            } else if(token.equals("(sqrt")) {
                return new sqrt(parse(q));
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

    private class neg implements expr {
        expr e;

        neg(expr e) {
            this.e = e;
        }

        public double eval() {
            return - e.eval();
        }

        public String toString() {
            return "-" + e.eval();
        }
    }

    private class add implements expr {
        Queue<expr> exprs;

        add(expr... expr_args) {
            this.exprs = new LinkedList<>();
            for(expr e : expr_args)
                exprs.add(e);
        }

        public double eval() {
            Iterator<expr> it = exprs.iterator();
            double ret = it.next().eval();
            while(it.hasNext())
                ret += it.next().eval();
            return ret;
        }

        public String toString() {
            Iterator<expr> it = exprs.iterator();
            String ret = it.next().toString();
            while(it.hasNext())
                ret += " + " + it.next().toString();
            return ret + ")";
        }
    }

    private class sub implements expr {
        Queue<expr> exprs;

        sub(expr... expr_args) {
            exprs = new LinkedList<>();
            for(expr e : expr_args)
                exprs.add(e);
        }

        public double eval() {
            Iterator<expr> it = exprs.iterator();
            double ret = it.next().eval();
            while(it.hasNext())
                ret -= it.next().eval();
            return ret;
        }

        public String toString() {
            Iterator<expr> it = exprs.iterator();
            String ret = it.next().toString();
            while(it.hasNext())
                ret += " - " + it.next().toString();
            return ret + ")";
        }
    }

    private class mul implements expr {
        Queue<expr> exprs;

        mul(expr... expr_args) {
            exprs = new LinkedList<>();
            for(expr e : expr_args)
                exprs.add(e);
        }

        public double eval() {
            Iterator<expr> it = exprs.iterator();
            double ret = it.next().eval();
            while(it.hasNext())
                ret *= it.next().eval();
            return ret;
        }

        public String toString() {
            Iterator<expr> it = exprs.iterator();
            String ret = it.next().toString();
            while(it.hasNext())
                ret += " * " + it.next().toString();
            return ret + ")";
        }
    }

    private class div implements expr {
        Queue<expr> exprs;

        div(expr... expr_args) {
            exprs = new LinkedList<>();
            for(expr e : expr_args)
                exprs.add(e);
        }

        public double eval() {
            Iterator<expr> it = exprs.iterator();
            double ret = it.next().eval();
            while(it.hasNext())
                ret /= it.next().eval();
            return ret;
        }

        public String toString() {
            Iterator<expr> it = exprs.iterator();
            String ret = it.next().toString();
            while(it.hasNext())
                ret += " * " + it.next().toString();
            return ret + ")";
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
