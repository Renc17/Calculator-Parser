import java.io.InputStream;
import java.io.IOException;
import java.lang.Math;


class TernaryEvaluator {
    private final InputStream in;
    private int lookahead;

    public TernaryEvaluator(InputStream in) throws IOException {
        this.in = in;
        lookahead = in.read();
    }

    private void consume(int symbol) throws IOException, ParseError {
        if (lookahead == symbol)
            lookahead = in.read();
        else
            throw new ParseError();
    }

    private boolean isDigit(int c) {
        return '0' <= c && c <= '9';
    }
    private boolean isEnd(int c) { return c == '\n' || c == -1; }
    private boolean isLPAREN(int c) { return c == '('; }
    private boolean isRPAREN(int c) { return c == ')'; }
    private boolean isStar(int c) { return c == '*'; }

    private int evalDigit(int c) {
        return c - '0';
    }

    public int eval() throws IOException, ParseError {
        int value = S();

        if (lookahead != -1 && lookahead != '\n')
            throw new ParseError();

        return value;
    }

    private int S() throws IOException, ParseError {
        int x = Expr();
        if (isEnd(lookahead)) {
            return x;   //Result of Evaluation
        }

        throw new ParseError();
    }

    private int Expr() throws IOException, ParseError {

        int x = factor();
        return post_expr(x);
    }

    private int factor() throws IOException, ParseError{
        if (isDigit(lookahead)){
            return number();
        }else if (isLPAREN(lookahead)){
            consume(lookahead);
            int x = Expr();
            if (isRPAREN(lookahead)){
                consume(lookahead);
                return x;
            }
        }

        throw new ParseError();
    }

    private int number() throws IOException, ParseError{
        int result = 0;
        while (isDigit(lookahead)){
            result = result * 10 + evalDigit(lookahead);
            if (evalDigit(lookahead) == 0 && result == 0){
                throw new ParseError();
            }
            consume(lookahead);
        }
        return result;
    }

    private int exponential()throws IOException, ParseError{

        if (isStar(lookahead)) {
            consume(lookahead);
            return Expr();
        }

        throw new ParseError();
    }

    private int post_expr(int condition) throws IOException, ParseError {
        switch (lookahead) {
            case '+':
                consume('+');
                int next = factor();

                return post_expr(condition+next);
            case '-':
                consume('-');
                next = factor();

               return post_expr(condition-next);
            case '*':
                consume('*');
                next = exponential();

                return post_expr((int) Math.pow(condition, next));
            case -1:
            case '\n':
            case ')':
                return condition;
        }

        throw new ParseError();
    }
}
