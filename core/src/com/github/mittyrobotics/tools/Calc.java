package com.github.mittyrobotics.tools;

import java.util.Stack;

public class Calc {

    public static int pref(char ch) {
        switch (ch) {
            case '+': case '-': return 1;
            case '*': case '/': return 2;
            case '^': return 3;
        }
        return -1;
    }

    public static String postfix(String s) {
        Stack<Character> stack = new Stack<>();
        StringBuilder ans = new StringBuilder();

        int n = 0;
        double operand = 0;
        boolean decimal = false;
        boolean negative = false;

        for(int i = 0; i < s.length(); i++) {
            char cur = s.charAt(i);

            boolean ignore = false;

            if(Character.isDigit(cur)) {
                if(!decimal) operand = 10 * operand + (cur - '0');
                else operand = operand + (Math.pow(10, -n) * (cur - '0'));
                n++;
            } else if (cur != ' ') {
                if (cur == '.') {
                    decimal = true;
                    n = 1;
                } else if (cur == '-' && n == 0) {
                    negative = true;
                    ignore = true;
                } else if (n != 0) {
                    if(negative) operand = -1 * operand;
                    ans.append(operand).append(" ");
                    operand = 0;
                    n = 0;
                    decimal = false;
                    negative = false;
                }

                if (cur == '(') {
                    stack.push(cur);
                } else if (cur == ')') {
                    while(!stack.isEmpty() && stack.peek() != '(') ans.append(stack.pop()).append(" ");
                    stack.pop();
                } else if (!(cur == '.') && !(ignore)) {
                    while(!stack.isEmpty() && pref(cur) <= pref(stack.peek())) {
                        ans.append(stack.pop()).append(" ");
                    }
                    stack.push(cur);
                }
            }
        }
        if(n != 0) {
            if(negative) operand = -operand;
            ans.append(operand).append(" ");
        }
        while(!stack.isEmpty()) ans.append(stack.pop()).append(" ");
        return ans.toString();
    }

    public static double postfixCalc(String s){
        Stack<Double> stack = new Stack<>();
        String[] sList = s.split(" ");
        double a = 0;
        double b = 0;

        for (String value : sList) {
            if(value.equals("*") || value.equals("/") || value.equals("+") || value.equals("-") || value.equals("^")) {
                a = stack.pop();
                b = stack.pop();
            }

            switch (value) {
                case "*":
                    stack.push(b * a);
                    break;
                case "/":
                    stack.push(b / a);
                    break;
                case "+":
                    stack.push(b + a);
                    break;
                case "-":
                    stack.push(b - a);
                    break;
                case "^":
                    stack.push(Math.pow(b, a));
                    break;
                default:
                    stack.push(Double.parseDouble(value));
                    break;
            }
        }
        return stack.pop();
    }

    public static double calc(String input) {
        return postfixCalc(postfix(input));
    }
}
