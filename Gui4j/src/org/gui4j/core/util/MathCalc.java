package org.gui4j.core.util;

/**
    This is the main source code for calculator written in Java 1.1
    This program emulates a "real" calculator in that is has a simular user
    interface arranged in a standard order. It provides an advantage over most
    of the other calculators because you can viewer both the equation and the
    results at the same time and allows for editing of the entered equation.
    Also this calculator adheres to the rules of arithmetic where equation are
    evaluated in the order brackets, exponents, multiplication, division,
    addition and subtraction.
 */
public class MathCalc
{
    String display = "";
    
    /**
     * This method clears all brackets from the expression beginning with the
     * inner most bracket and working outwords and from left to right for non
     * nested brackets.
     * @param swb
     * @return String
     */
    private static String brackets(String swb)
    {
        int lb; //holds the position of the left bracket
        int index; // gives position of the character under evaulation
        int count = 1; // variable used to determine when left bracket is closed
        char cb;
        String inside = "";

        lb = swb.indexOf('(');
        index = lb;

        while (index < swb.length())
        {
            cb = swb.charAt(++index);

            if (cb == '(')
            {
                count++; // count how many closing bracket needed
            }
            else if (cb == ')')
            {
                count--; // count how many closing bracket needed
            }
            if (cb == ')' && count == 0)
            { // bracket has been closed
                inside = swb.substring(lb + 1, index); // contents inside the bracket
                break;
            }
        }

        // send the contenst inside the bracket to be evalutated with one less
        // set of brackets
        if (lb > 0)
        {
            swb = swb.substring(0, lb) + evaluate(inside) + swb.substring(index + 1, swb.length());
        }
        else
        {
            swb = evaluate(inside) + swb.substring(index + 1, swb.length());
        }

        return swb;
    }

    /**
     * This method is the controlling method for the evaluation of the
     * mathematical equations
     * @param s
     * @return String
     */
    public static String evaluate(String s)
    {
        int n = 0;

        // insert * where implicit multiplication is assumed
        s = insertMultiply(s);

        // determine if either e or pi are in the equation replace with numbers
        if (s.indexOf('e') + s.indexOf("pi") > -2)
        {
            s = constants(s);
        }

        while (s.indexOf('(') != -1) // evaulate inside bracket first if present
        {
            s = brackets(s);
        }
        if (s.indexOf('^') != -1) // next evaluate all the exponents
        {
            s = exponent(s);
        }
        s = func(s); // functions such as cos, sin, log etc.

        s = resolve(s); // eliminate the combination of sigs etc +-
        if (s.indexOf('*') != -1 || s.indexOf('/') != -1)
        {
            s = mult(s);
        }
        s = resolve(s);
        if (s.indexOf('+') != -1)
        {
            s = sum(s); // do all the addition in the equation
        }
        else
        {
            n = s.indexOf('-', n);
            // evaluate all the substraction in the equation
            while (n != -1)
            {
                if (n != 0)
                {
                    if (s.charAt(n - 1) != 'E')
                    { // make sure that - sign not part of
                        //exponent
                        s = sum(s);
                        break;
                    }
                }
                n = s.indexOf('-', n + 1);
            }
        }
        return s; // return the final answer
    }

    /**
     * this method replaces constant variable such as e and pe with there
     * numeric values 2.71818... and 3.141259...
     * @param swc
     * @return String
     */
    private static String constants(String swc)
    {
        int a, b;
        a = swc.indexOf('e');

        if (a != -1)
        {
            swc = swc.substring(0, a) + Math.E + swc.substring(a + 1, swc.length());
        }

        b = swc.indexOf("pi");
        if (b != -1)
        {
            swc = swc.substring(0, b) + Math.PI + swc.substring(b + 2, swc.length());
        }

        if (swc.indexOf('e') + swc.indexOf("pi") > -2)
        {
            return constants(swc);
        }
        else
        {
            return swc;
        }
    }

    /**
     * This method evaluates a string containing only addition and
     * subtraction from left to right 
     * @param s
     * @return String
     */
    private static String sum(String s)
    {
        double dub1, dub2, result;
        char operation, c;
        int i = 0, n = 1;
        String num1 = "", num2 = "";

        c = s.charAt(0);
        if (c == '-')
        { // test if equation starts with a -ve
            i++;
            c = s.charAt(i);
            n = -1;
        }

        // find the first number
        while (Character.isDigit(c) || c == '.' || c == 'E' || (c == '-' && s.charAt(i - 1) == 'E'))
        {
            num1 += c;
            i++;
            c = s.charAt(i);
        }

        // next character determines the operation that will be performed
        operation = c;
        i++;
        c = s.charAt(i);
        num2 += c;

        // find the number after the operator
        while ((Character.isDigit(c) || c == '.' || c == 'E' || (c == '-' && s.charAt(i - 1) == 'E'))
            && i < s.length() - 1)
        {
            c = s.charAt(++i);
            if (c == '+' || (c == '-' && (s.charAt(i - 1) != 'E')))
            {
                break;
            }
            num2 += c;
        }

        // convert the numbers to double values
        dub1 = Double.valueOf(num1).doubleValue() * n;
        dub2 = Double.valueOf(num2).doubleValue();

        if (operation == '+')
        {
            result = dub1 + dub2;
        }
        else
        {
            result = dub1 - dub2;
        }

        if (i == s.length() - 1)
        {
            s = String.valueOf(result);
            return s;
        }
        else
        {
            s = String.valueOf(result) + s.substring(i, s.length());
            return sum(s);
        }
    }

    /**
     * This method deals with multiplication and division in a string and
     * @param s
     * @return String
     */
    private static String mult(String s)
    {
        int m, d, start, left, index;
        boolean multiply;
        double dub1, dub2, answer;
        char c;
        String num1 = "", num2 = "";

        m = s.indexOf('*');
        d = s.indexOf('/');

        // determine if multiplication or division
        if (((m < d) && (m != -1)) || ((m > d) && (d == -1)))
        {
            start = m;
            multiply = true;
        }
        else
        {
            start = d;
            multiply = false;
        }

        index = start - 1;
        c = s.charAt(index);

        // determine the value of num1
        while (Character.isDigit(c)
            || c == '.'
            || (index == 0)
            || c == 'E'
            || (c == '-' && s.charAt(index - 1) == 'E'))
        {
            num1 = c + num1;
            if (index == 0)
            {
                break;
            }
            c = s.charAt(--index);
        }

        if (index == 0)
        {
            left = 0;
        }
        else
        {
            left = ++index;
        }

        index = start + 1;
        c = s.charAt(index);

        // determine the value of num2
        while (Character.isDigit(c)
            || c == '.'
            || ((index == start + 1) && (c == '-'))
            || (c == '-' && s.charAt(index - 1) == 'E'))
        {
            num2 += c;
            if (index == s.length() - 1)
            {
                break;
            }
            c = s.charAt(++index);
        }

        //  perform calculation

        dub1 = Double.valueOf(num1).doubleValue();
        dub2 = Double.valueOf(num2).doubleValue();

        if (multiply)
        {
            answer = dub1 * dub2;
        }
        else
        {
            answer = dub1 / dub2;
        }

        // insert the answer into the equation
        if (index == s.length() - 1)
        {
            s = s.substring(0, left) + String.valueOf(answer);
        }
        else
        {
            s = s.substring(0, left) + String.valueOf(answer) + s.substring(index, s.length());
        }

        if (s.indexOf('*') != -1 || s.indexOf('/') != -1)
        {
            return mult(s);
        }
        else
        {
            return s;
        }
    }

    /**
     * This method deals with exponents in the string
     * @param se
     * @return String
     */
    private static String exponent(String se)
    {
        int index, m, k = 1, left;
        char ce;
        double base, power, result;
        String num1 = "", num2 = "";

        m = se.indexOf('^'); // index of the exponent operator
        index = m - 1;
        ce = se.charAt(index); // char that is under investiogation

        // find the value of the base
        while (Character.isDigit(ce) || ce == '.' || ce == 'E' || (ce == '-' && se.charAt(index - 1) == 'E'))
        {
            num1 = ce + num1; // append the character before the #
            if (index == 0)
            {
                break;
            }

            ce = se.charAt(--index);
        }

        // determine if the base is a negative number
        if ((index > 1) && (se.charAt(index) == '-') && !(Character.isDigit(se.charAt(index - 1))))
        {
            k = -1;
            left = index;
        }
        else if (index == 0)
        {
            if (ce == '-')
            {
                k = -1;
            }
            left = 0;
        }
        else
        {
            left = index + 1;
        }

        index = m + 1;
        ce = se.charAt(index);

        // find the value of the exponent
        while ((Character.isDigit(ce) || (ce == '-' && index == m + 1) || ce == '.')
            || ce == 'E'
            || (ce == '-' && se.charAt(index - 1) == 'E'))
        {
            num2 += ce;
            if (index == se.length() - 1)
            {
                break;
            }
            ce = se.charAt(++index);
        }

        // convert num1 and num2 to doubles and evaluate the result
        base = Double.valueOf(num1).doubleValue() * k;
        power = Double.valueOf(num2).doubleValue();
        result = Math.pow(base, power);
        // insert the result into the equation
        se = replace(se, left, index, result);

        if (se.indexOf('^') != -1)
        {
            return exponent(se);
        }
        else
        {
            return se;
        }
    }

    /**
     * This method evaluates all the functions in the expression
     * filler line to see what happens
     * another filler line a third filler line
     * @param sf
     * @return String
     */
    private static String func(String sf)
    {
        int total = 0, temp;
        String fnctn[] = { "sin", "cos", "tan", "log", "ln", "sqrt", "!" }, temp2 = "";
        // array that holds the names of funcs.
        int pos[] = new int[7]; // array that will hold the pos. of funcs. 

        for (int n = 0; n < fnctn.length; n++)
        {
            pos[n] = sf.lastIndexOf(fnctn[n]); // initialize array
        }

        for (int m = 0; m < fnctn.length; m++)
        {
            total += pos[m]; // find total of all the position values
        }

        if (total == -7) // determine if the equation holds any functions
        {
            return sf; // equation does not have any functions so return
        }

        // sort the array to ensure that the functions are evaluated from
        // right to left so that functions can be nested
        for (int i = pos.length; i > 1; i--)
        {
            for (int j = 0; j < i - 1; j++)
            {

                if (pos[j] < pos[j + 1])
                { // sort the arrays
                    temp = pos[j];
                    pos[j] = pos[j + 1];
                    pos[j + 1] = temp;

                    temp2 = fnctn[j];
                    fnctn[j] = fnctn[j + 1];
                    fnctn[j + 1] = temp2;

                }
            }
        }

        // find the right most function and evaluate it and puts its
        // numeric equivalent in the equation 
        if (fnctn[0].equals("sin"))
        {
            if ((pos[0] == 0 || sf.charAt(pos[0] - 1) != 'a'))
            {
                return func(Functions.sine(sf, pos[0], false));
            }
            else
            {
                return func(Functions.asin(sf, pos[0], false));
            }
        }
        else if (fnctn[0].equals("cos"))
        {
            if ((pos[0] == 0 || sf.charAt(pos[0] - 1) != 'a'))
            {
                return func(Functions.cosine(sf, pos[0], false));
            }
            else
            {
                return func(Functions.acos(sf, pos[0], false));
            }
        }
        else if (fnctn[0].equals("tan"))
        {
            if ((pos[0] == 0 || sf.charAt(pos[0] - 1) != 'a'))
            {
                return func(Functions.tangent(sf, pos[0], false));
            }
            else
            {
                return func(Functions.atan(sf, pos[0], false));
            }
        }
        else if (fnctn[0].equals("log"))
        {
            return func(Functions.logarithm(sf, pos[0]));
        }
        else if (fnctn[0].equals("ln"))
        {
            return func(Functions.lnat(sf, pos[0]));
        }
        else if (fnctn[0].equals("sqrt"))
        {
            return func(Functions.sqroot(sf, pos[0]));
        }
        else
        {
            return func(Functions.factorial(sf, pos[0]));
        }
    }

    /**
     * replaces the portion of the equation between r and l with the
     * number represented by d 
     * @param sb
     * @param l
     * @param r
     * @param d
     * @return String
     */
    private static String replace(String sb, int l, int r, double d)
    {
        if (r == sb.length() - 1)
        {
            sb = sb.substring(0, l) + String.valueOf(d);
        }
        else
        {
            sb = sb.substring(0, l) + String.valueOf(d) + sb.substring(r, sb.length());
        }
        return sb;
    }

    /**
     * insert the * in all positions of the equation of implicit multiplication
     * @param ins
     * @return String
     */
    private static String insertMultiply(String ins)
    {
        int q = 0;
        char c1, c2;

        while (q < ins.length() - 1)
        {
            c1 = ins.charAt(q);
            c2 = ins.charAt(q + 1);

            // account for all combinations of letters and numbers
            if (Character.isDigit(c1) && (Character.isLetter(c2) || c2 == '(') && c2 != 'E')
            {
                ins = ins.substring(0, q + 1) + '*' + ins.substring(q + 1);
            }
            else if (Character.isDigit(c2) && c1 == ')')
            {
                ins = ins.substring(0, q + 1) + '*' + ins.substring(q + 1);
            }
            else if (
                (c1 == 'e' || c1 == 'i' || c1 == ')')
                    && (c2 == 'e' || c2 == 'p' || c2 == '(' || Character.isLetter(c2))
                    && c2 != 'n')
            {
                ins = ins.substring(0, q + 1) + '*' + ins.substring(q + 1);
            }
            q++;
        }
        return ins;
    }

    /**
     * replace all combinations of signs eg. +- changed to -
     * @param str
     * @return String
     */
    private static String resolve(String str)
    {
        char a, b;
        int k = 0;

        while (k < str.length() - 1)
        {
            a = str.charAt(k);
            b = str.charAt(k + 1);
            if ((a == '+' || a == '-') && (b == '+' || b == '-'))
            {
                if ((a == '+' && b == '+') || (a == '-' && b == '-'))
                {
                    str = str.substring(0, k) + '+' + str.substring(k + 2, str.length());
                }
                else
                {
                    str = str.substring(0, k) + '-' + str.substring(k + 2, str.length());
                }
            }
            k++;
        }
        return str;
    }
    
    // ************************************************************************************


    /**
        This class is a helper class to Calc which evaluates the functions that
        might appear in the equation. The functions replace the function name and
        arguement and replace it with it's  number value. The trigonometric
        functions can take arguments in either radians or degrees and return either
        radian or degrees.
     */
    private static class Functions
    {

        /**
         * calculate the sines in the equation
         * @param ss
         * @param p
         * @param b
         * @return String
         */
        protected static String sine(String ss, int p, boolean b)
        {
            double theta, result;
            String num = "";

            num = getNumber(ss, p + 3);
            theta = Double.valueOf(num).doubleValue();
            if (b)
            {
                result = Math.sin(theta);
            }
            else
            {
                result = Math.sin(theta * Math.PI / 180);
            }
            ss =
                ss.substring(0, p) + String.valueOf(result) + ss.substring(p + 3 + num.length(), ss.length());
            return ss;
        }

        /**
         * calculate the cosines in the equation
         * @param sc
         * @param m
         * @param b
         * @return String
         */
        protected static String cosine(String sc, int m, boolean b)
        {
            double psi, result;
            String num = "";

            num = getNumber(sc, m + 3);
            psi = Double.valueOf(num).doubleValue();
            if (b)
            {
                result = Math.cos(psi);
            }
            else
            {
                result = Math.cos(psi * Math.PI / 180);
            }
            sc =
                sc.substring(0, m) + String.valueOf(result) + sc.substring(m + 3 + num.length(), sc.length());
            return sc;
        }

        /**
         * calculate the tangents in the equation
         * @param st
         * @param n
         * @param b
         * @return String
         */
        protected static String tangent(String st, int n, boolean b)
        {
            double rho, result;
            String num = "";

            num = getNumber(st, n + 3);
            rho = Double.valueOf(num).doubleValue();
            if (b)
            {
                result = Math.tan(rho);
            }
            else
            {
                result = Math.tan(rho * Math.PI / 180);
            }
            st =
                st.substring(0, n) + String.valueOf(result) + st.substring(n + 3 + num.length(), st.length());
            return st;
        }

        /**
         * calculate the inverse sines in the equation
         * @param sa
         * @param a
         * @param q
         * @return String
         */
        protected static String asin(String sa, int a, boolean q)
        {
            double angle, arg;
            String number;

            number = getNumber(sa, a + 3);
            arg = Double.valueOf(number).doubleValue();
            if (q)
            {
                angle = Math.asin(arg);
            }
            else
            {
                angle = Math.asin(arg) * 180 / Math.PI;
            }
            sa =
                sa.substring(0, a - 1)
                    + String.valueOf(angle)
                    + sa.substring(a + 3 + number.length(), sa.length());
            return sa;
        }

        /**
         * calculate the inverse consines in the equation
         * @param sac
         * @param c
         * @param p
         * @return String
         */
        protected static String acos(String sac, int c, boolean p)
        {
            double angle, arg;
            String number;

            number = getNumber(sac, c + 3);
            arg = Double.valueOf(number).doubleValue();
            if (p)
            {
                angle = Math.acos(arg);
            }
            else
            {
                angle = Math.acos(arg) * 180 / Math.PI;
            }
            sac =
                sac.substring(0, c - 1)
                    + String.valueOf(angle)
                    + sac.substring(c + 3 + number.length(), sac.length());
            return sac;
        }

        /**
         * calculate the inverse tangents in the equation
         * @param sat
         * @param b
         * @param z
         * @return String
         */
        protected static String atan(String sat, int b, boolean z)
        {
            double angle, arg;
            String number;

            number = getNumber(sat, b + 3);
            arg = Double.valueOf(number).doubleValue();
            if (z)
            {
                angle = Math.atan(arg);
            }
            else
            {
                angle = Math.atan(arg) * 180 / Math.PI;
            }
            sat =
                sat.substring(0, b - 1)
                    + String.valueOf(angle)
                    + sat.substring(b + 3 + number.length(), sat.length());
            return sat;
        }

        /**
         * calculate the logarithms to the base ten in the equation
         * @param sl
         * @param w
         * @return String
         */
        protected static String logarithm(String sl, int w)
        {
            double arg, result;
            String numl = "";

            numl = getNumber(sl, w + 3);
            arg = Double.valueOf(numl).doubleValue();
            result = Math.log(arg) / Math.log(10);
            sl =
                sl.substring(0, w)
                    + String.valueOf(result)
                    + sl.substring(w + 3 + numl.length(), sl.length());
            return sl;
        }

        /**
         * calculate the logarithms to the base e in the equation
         * @param sln
         * @param y
         * @return String
         */
        protected static String lnat(String sln, int y)
        {
            double real, result;
            String numln = "";

            numln = getNumber(sln, y + 2);
            real = Double.valueOf(numln).doubleValue();
            result = Math.log(real);
            sln =
                sln.substring(0, y)
                    + String.valueOf(result)
                    + sln.substring(y + 2 + numln.length(), sln.length());
            return sln;
        }

        /**
         * calculate the square roots in the equation
         * @param sq
         * @param a
         * @return String
         */
        protected static String sqroot(String sq, int a)
        {
            double arg, result;
            String numsq = "";

            numsq = getNumber(sq, a + 4);
            arg = Double.valueOf(numsq).doubleValue();
            result = Math.sqrt(arg);
            sq =
                sq.substring(0, a)
                    + String.valueOf(result)
                    + sq.substring(a + 4 + numsq.length(), sq.length());
            result = Math.sqrt(arg);
            return sq;
        }

        /**
         * get the number that is the arguement to the functions
         * @param sn
         * @param l
         * @return String
         */
        protected static String getNumber(String sn, int l)
        {
            char cn;
            int index = l;
            String anumber = "";

            cn = sn.charAt(l);
            while (Character.isDigit(cn) || cn == '.' || (cn == '-' && index == l))
            {
                anumber += cn;

                if (index == sn.length() - 1)
                {
                    break;
                }

                cn = sn.charAt(++index);
            }
            return anumber;
        }

        /**
         * evaluate a factorial that may be in the equation
         * @param sf
         * @param p
         * @return String
         */
        // 
        protected static String factorial(String sf, int p)
        {
            char cf;
            int index = p - 1, arg, right = 0;
            String num = "";

            cf = sf.charAt(index);
            // test if the factorial is at the beginning of the equation
            while (Character.isDigit(cf))
            {
                num = cf + num;
                if (index == 0)
                {
                    right = 0;
                    return getFact(Integer.parseInt(num)) + sf.substring(num.length() + 1);
                }
                cf = sf.charAt(--index);
                right = index + 1;
            }
            arg = Integer.parseInt(num);
            sf = sf.substring(0, right) + getFact(arg) + sf.substring(index + num.length() + 2);
            return sf;
        }

        /**
         * use recursion to evaluate a factorial
         * @param a
         * @return double
         */
        protected static double getFact(int a)
        {

            if (a == 1)
            {
                return 1;
            }
            else
            {
                return a * getFact(a - 1);
            }
        }
    }

}
