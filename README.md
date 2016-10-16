calc
====
An infix notation calculator.  Precedence is determined by the parentheses included in the input.

Adding `--verbose` or `-v` to the program arguments will allow you to see the Reverse-Polish notation and abstract syntax tree debug information.

Below is an example usage:
```
$ javac calc.java && java calc
> 2 + 2
4.0
> (2 * 2) + 2
6.0
> (2 + 2) * 2
8.0
> pi
3.141592653589793
> e
2.718281828459045
> e^2
7.3890560989306495
> sin(pi^2)
-0.4303012170000917
> min(2,3) / 2
1.0
```
