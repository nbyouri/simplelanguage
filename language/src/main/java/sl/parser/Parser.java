/* Generated By:JavaCC: Do not edit this line. Parser.java */
package sl.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

import com.oracle.truffle.api.source.Source;
import sl.SLLanguage;
import sl.nodes.SLExpressionNode;
import sl.nodes.SLRootNode;
import sl.nodes.SLStatementNode;

public class Parser implements ParserConstants {
    SLNodeFactory factory = null;

    public Map<String, SLRootNode> parseUri(Source source) throws Exception {
        return parseUri(null, source);
    }

    public Map<String, SLRootNode> parseUri(SLLanguage language, Source source) throws Exception {
        this.factory = new SLNodeFactory(language, source);
            this.Uri();

                if (this.errors.size() > 0) {
                    StringBuilder msg = new StringBuilder("Error(s) parsing script:\u005cn");
                    for (ErrorDescription error : this.errors) {
                        msg.append(error.message).append("\u005cn");
                    }
                    final ErrorDescription desc = errors.get(0);
                    throw new SLParseError(
                            source,
                            desc.line,
                            desc.column,
                            desc.length,
                            msg.toString());
                }
        return this.factory.getAllFunctions();
    }

    final class ErrorDescription {
        final int line;
        final int column;
        final int length;
        final String message;

        ErrorDescription(final int line, final int column, final int length, final String message) {
            this.line = line;
            this.column = column;
            this.length = length;
            this.message = message;
        }
    }

    protected final List<ErrorDescription> errors = new ArrayList<ErrorDescription>();

    public String errMsgFormat = "-- line {0} col {1}: {2}"; // 0=line, 1=column, 2=text

    protected void printMsg(int line, int column, int length, String msg) {
        StringBuffer b = new StringBuffer(errMsgFormat);
        int pos = b.indexOf("{0}");
        if (pos >= 0) {
            b.delete(pos, pos + 3);
            b.insert(pos, line);
        }
        pos = b.indexOf("{1}");
        if (pos >= 0) {
            b.delete(pos, pos + 3);
            b.insert(pos, column);
        }
        pos = b.indexOf("{2}");
        if (pos >= 0)
            b.replace(pos, pos + 3, msg);
        errors.add(new ErrorDescription(line, column, length, b.toString()));
    }

        private void SynErr( ParseException e ) {
//            token = getNextToken();
            // Get the possible expected tokens
            StringBuffer expected = new StringBuffer();
            for ( int i = 0; i < e.expectedTokenSequences.length; i++ ) {
                for ( int j = 0; j < e.expectedTokenSequences[ i ].length;
                    j++ ) {
                    expected.append( "\u005cn" );
                    expected.append( "    " );
                    expected.append( tokenImage[
                        e.expectedTokenSequences[ i ][ j ] ] );
                    expected.append( "..." );
                }
            }

            // Print error message
            if ( e.expectedTokenSequences.length == 1 ) {
                this.SemErr(String.format( "%s found where %s sought",
                    getToken( 1 ), expected ));
            }
            else {
                this.SemErr(String.format( "%s found where one of %s sought",
                    getToken( 1 ), expected ));
            }
        }

    void SynErr(String msg) {
//        token = getNextToken();
        this.SemErr(msg);
    }

    void FatalError(String msg) {
        errors.add(new ErrorDescription(0, 0, 0, msg));
    }

    void SemErr(String msg) {
        this.SemErr(token.beginLine, token.beginColumn, token.val.length(), msg);
    }

    void SemErr(int line, int col, int length, String s) {
        printMsg(line, col, length, s);
    }

    void Warning(int line, int col, int length, String s) {
        printMsg(line, col, length, s);
    }

/////////////////////////////////////////////////////////
//       The uri syntactic grammar starts here         //
/////////////////////////////////////////////////////////


// Parse a compilation unit
//       translationUnit ::= {function}
  final public void Uri() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case FUNCTION:
      label_1:
      while (true) {
        Function();
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case FUNCTION:
          ;
          break;
        default:
          jj_la1[0] = jj_gen;
          break label_1;
        }
      }
      break;
    default:
      jj_la1[1] = jj_gen;
      throw new ParseException("No functions");
    }
  }

// Parse a function
//      Function ::= FUNCTION IDENTIFIER LPAREN [IDENTIFIER {COMMA IDENTIFIER}] RPAREN block
  final public void Function() throws ParseException {
    try {
      jj_consume_token(FUNCTION);
      jj_consume_token(IDENTIFIER);
                              Token identifierToken = token;
      jj_consume_token(LPAREN);
                              int bodyStartPos = token.charPos;
                              factory.startFunction(identifierToken, bodyStartPos);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case IDENTIFIER:
        jj_consume_token(IDENTIFIER);
                               factory.addFormalParameter(token);
        label_2:
        while (true) {
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case COMMA:
            ;
            break;
          default:
            jj_la1[2] = jj_gen;
            break label_2;
          }
          jj_consume_token(COMMA);
          jj_consume_token(IDENTIFIER);
                                   factory.addFormalParameter(token);
        }
        break;
      default:
        jj_la1[3] = jj_gen;
        ;
      }
      jj_consume_token(RPAREN);
    } catch (ParseException e) {
        {if (true) throw new ParseException("Failed to parse function");}
    }
      factory.finishFunction(Block(false));
  }

// Parse a block
//      Block ::= LCURLY {blockStatement} RCURLY
  final public SLStatementNode Block(boolean inLoop) throws ParseException {
    SLStatementNode node = null;
    List<SLStatementNode> body = new ArrayList<SLStatementNode>();
    factory.startBlock();
    int start = 0;
    try {
      jj_consume_token(LCURLY);
                   start = token.charPos;
      label_3:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case RETURN:
        case IF:
        case WHILE:
        case DEBUGGER:
        case BREAK:
        case CONTINUE:
        case LPAREN:
        case IDENTIFIER:
        case NUMERIC_LITERAL:
        case STRING_LITERAL:
          ;
          break;
        default:
          jj_la1[4] = jj_gen;
          break label_3;
        }
        node = Statement(inLoop);
              body.add(node);
      }
      jj_consume_token(RCURLY);
    } catch (ParseException e) {
        SynErr(e);
    }
      int length = (token.charPos + token.val.length()) - start;
      {if (true) return factory.finishBlock(body, start, length);}
    throw new Error("Missing return statement in function");
  }

//
//
// Parse a statement
//     statement ::= block
//                | IF parExpression statement [ELSE statement]
//                | WHILE parExpression statement
//                | RETURN [expression] SEMI
//                | SEMI
//                | expression SEMI // TODO validate side effects
  final public SLStatementNode Statement(boolean inLoop) throws ParseException {
    SLStatementNode result = null;
    SLExpressionNode expr = null;
    SLStatementNode body = null;
    SLStatementNode elsePart = null;
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case WHILE:
        jj_consume_token(WHILE);
                  Token whileToken = token;
        jj_consume_token(LPAREN);
        expr = Expression();
        jj_consume_token(RPAREN);
        body = Block(true);
          result = factory.createWhile(whileToken, expr, body);
        break;
      case BREAK:
        jj_consume_token(BREAK);
                    if (inLoop) {
                        result = factory.createBreak(token);
                    } else {
                        SemErr("break used outside of loop");
                    }
        jj_consume_token(SEMI);
        break;
      case CONTINUE:
        jj_consume_token(CONTINUE);
                       if (inLoop) {
                           result = factory.createContinue(token);
                       } else {
                           SemErr("continue used outside of loop");
                       }
        jj_consume_token(SEMI);
        break;
      case IF:
        jj_consume_token(IF);
                 Token ifToken = token;
        jj_consume_token(LPAREN);
        expr = Expression();
        jj_consume_token(RPAREN);
        body = Block(inLoop);
        if (jj_2_1(2147483647)) {
          jj_consume_token(ELSE);
          elsePart = Block(inLoop);
        } else {
          ;
        }
            result = factory.createIf(ifToken, expr, body, elsePart);
        break;
      case RETURN:
        jj_consume_token(RETURN);
                     Token returnToken = token;
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case LPAREN:
        case IDENTIFIER:
        case NUMERIC_LITERAL:
        case STRING_LITERAL:
          expr = Expression();
          break;
        default:
          jj_la1[5] = jj_gen;
          ;
        }
            result = factory.createReturn(returnToken, expr);
        jj_consume_token(SEMI);
        break;
      case LPAREN:
      case IDENTIFIER:
      case NUMERIC_LITERAL:
      case STRING_LITERAL:
        result = Expression();
        jj_consume_token(SEMI);
        break;
      case DEBUGGER:
        jj_consume_token(DEBUGGER);
            result = factory.createDebugger(token);
        jj_consume_token(SEMI);
        break;
      default:
        jj_la1[6] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    } catch (ParseException e) {
        SynErr("invalid Statement");
    }
      {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

// Parse an expression
  final public SLExpressionNode Expression() throws ParseException {
   SLExpressionNode result = null;
   SLExpressionNode right = null;
    try {
      result = LogicTerm();
      label_4:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case LOR:
          ;
          break;
        default:
          jj_la1[7] = jj_gen;
          break label_4;
        }
        jj_consume_token(LOR);
                      Token op = token;
        right = LogicTerm();
              result = factory.createBinary(op, result, right);
      }
    } catch (ParseException e) {
        SynErr(e);
    }
      {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  final public SLExpressionNode LogicTerm() throws ParseException {
   SLExpressionNode result = null;
   SLExpressionNode right = null;
    try {
      result = LogicFactor();
      label_5:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case LAND:
          ;
          break;
        default:
          jj_la1[8] = jj_gen;
          break label_5;
        }
        jj_consume_token(LAND);
                       Token op = token;
        right = LogicFactor();
              result = factory.createBinary(op, result, right);
      }
    } catch (ParseException e) {
        SynErr(e);
    }
      {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  final public SLExpressionNode LogicFactor() throws ParseException {
   SLExpressionNode result = null;
   SLExpressionNode right = null;
    try {
      result = Arithmetic();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case EQUAL:
      case NOT_EQUAL:
      case GT:
      case GE:
      case LT:
      case LE:
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case LT:
          jj_consume_token(LT);
          break;
        case LE:
          jj_consume_token(LE);
          break;
        case GT:
          jj_consume_token(GT);
          break;
        case GE:
          jj_consume_token(GE);
          break;
        case EQUAL:
          jj_consume_token(EQUAL);
          break;
        case NOT_EQUAL:
          jj_consume_token(NOT_EQUAL);
          break;
        default:
          jj_la1[9] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
                                                                   Token op = token;
        right = Arithmetic();
              result = factory.createBinary(op, result, right);
        break;
      default:
        jj_la1[10] = jj_gen;
        ;
      }
    } catch (ParseException e) {
        SynErr(e);
    }
      {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  final public SLExpressionNode Arithmetic() throws ParseException {
   SLExpressionNode result = null;
   SLExpressionNode right = null;
    try {
      result = Term();
      label_6:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case PLUS:
        case MINUS:
          ;
          break;
        default:
          jj_la1[11] = jj_gen;
          break label_6;
        }
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case PLUS:
          jj_consume_token(PLUS);
          break;
        case MINUS:
          jj_consume_token(MINUS);
          break;
        default:
          jj_la1[12] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
                                   Token op = token;
        right = Term();
              result = factory.createBinary(op, result, right);
      }
    } catch (ParseException e) {
        SynErr(e);
    }
      {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  final public SLExpressionNode Term() throws ParseException {
   SLExpressionNode result = null;
   SLExpressionNode right = null;
    try {
      result = Factor();
      label_7:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case STAR:
        case DIV:
          ;
          break;
        default:
          jj_la1[13] = jj_gen;
          break label_7;
        }
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case STAR:
          jj_consume_token(STAR);
          break;
        case DIV:
          jj_consume_token(DIV);
          break;
        default:
          jj_la1[14] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
                                 Token op = token;
        right = Factor();
              result = factory.createBinary(op, result, right);
      }
    } catch (ParseException e) {
        SynErr(e);
    }
      {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  final public SLExpressionNode Factor() throws ParseException {
    SLExpressionNode result = null;
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case IDENTIFIER:
        jj_consume_token(IDENTIFIER);
                           SLExpressionNode assignmentName = factory.createStringLiteral(token, false);
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case ASSIGN:
        case LPAREN:
        case LBRACK:
        case DOT:
          result = MemberExpression(null, null, assignmentName);
          break;
        default:
          jj_la1[15] = jj_gen;
                    result = factory.createRead(assignmentName);
        }
        break;
      case STRING_LITERAL:
        jj_consume_token(STRING_LITERAL);
                             result = factory.createStringLiteral(token, true);
        break;
      case NUMERIC_LITERAL:
        jj_consume_token(NUMERIC_LITERAL);
                              result = factory.createNumericLiteral(token);
        break;
      case LPAREN:
        jj_consume_token(LPAREN);
                     int start = token.charPos;
        result = Expression();
                                  SLExpressionNode expr = result;
        jj_consume_token(RPAREN);
                     int length = (token.charPos + token.val.length()) - start;
            result = factory.createParenExpression(expr, start, length);
        break;
      default:
        jj_la1[16] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    } catch (ParseException e) {
        SynErr("invalid Factor");
    }
      {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  final public SLExpressionNode MemberExpression(SLExpressionNode r,
                                    SLExpressionNode assignmentReceiver,
                                    SLExpressionNode assignmentName) throws ParseException {
    SLExpressionNode result = null;
    SLExpressionNode receiver = r;
    SLExpressionNode nestedAssignmentName = null;
    List<SLExpressionNode> parameters = null;
    SLExpressionNode parameter = null;
    try {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case LPAREN:
        jj_consume_token(LPAREN);
                       parameters = new ArrayList<SLExpressionNode>();
                        if (receiver == null) {
                            receiver = factory.createRead(assignmentName);
                        }
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case LPAREN:
        case IDENTIFIER:
        case NUMERIC_LITERAL:
        case STRING_LITERAL:
          parameter = Expression();
                  parameters.add(parameter);
          label_8:
          while (true) {
            switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
            case COMMA:
              ;
              break;
            default:
              jj_la1[17] = jj_gen;
              break label_8;
            }
            jj_consume_token(COMMA);
            parameter = Expression();
                      parameters.add(parameter);
          }
          break;
        default:
          jj_la1[18] = jj_gen;
          ;
        }
        jj_consume_token(RPAREN);
                       Token finalToken = token;
              result = factory.createCall(receiver, parameters, finalToken);
        break;
      case ASSIGN:
        jj_consume_token(ASSIGN);
                         SLExpressionNode value = null;
        value = Expression();
              if (assignmentName == null) {
                SemErr("invalid assignment target");
              } else if (assignmentReceiver == null) {
                result = factory.createAssignment(assignmentName, value);
              } else {
                result = factory.createWriteProperty(assignmentReceiver, assignmentName, value);
              }
        break;
      case DOT:
        jj_consume_token(DOT);
                  if (receiver == null) {
                    receiver = factory.createRead(assignmentName);
                  }
        jj_consume_token(IDENTIFIER);
            nestedAssignmentName = factory.createStringLiteral(token, false);
            result = factory.createReadProperty(receiver, nestedAssignmentName);
        break;
      case LBRACK:
        jj_consume_token(LBRACK);
                     if (receiver == null) {
                        receiver = factory.createRead(assignmentName);
                     }
        nestedAssignmentName = Expression();
            result = factory.createReadProperty(receiver, nestedAssignmentName);
        jj_consume_token(RBRACK);
        break;
      default:
        jj_la1[19] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case ASSIGN:
      case LPAREN:
      case LBRACK:
      case DOT:
        result = MemberExpression(result, receiver, nestedAssignmentName);
        break;
      default:
        jj_la1[20] = jj_gen;
        ;
      }
    } catch (ParseException e) {
        SynErr("invalid MemberExpression");
    }
      {if (true) return result;}
    throw new Error("Missing return statement in function");
  }

  private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  private boolean jj_3_1() {
    if (jj_scan_token(ELSE)) return true;
    return false;
  }

  /** Generated Token Manager. */
  public ParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  private int jj_gen;
  final private int[] jj_la1 = new int[21];
  static private int[] jj_la1_0;
  static private int[] jj_la1_1;
  static {
      jj_la1_init_0();
      jj_la1_init_1();
   }
   private static void jj_la1_init_0() {
      jj_la1_0 = new int[] {0x100,0x100,0x0,0x0,0x2000f600,0x20000000,0x2000f600,0x8000000,0x10000000,0x7e0000,0x7e0000,0x6000000,0x6000000,0x1800000,0x1800000,0x20010000,0x20000000,0x0,0x20000000,0x20010000,0x20010000,};
   }
   private static void jj_la1_init_1() {
      jj_la1_1 = new int[] {0x0,0x0,0x10,0x40,0xc40,0xc40,0xc40,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x0,0x22,0xc40,0x10,0xc40,0x22,0x22,};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[1];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  /** Constructor with InputStream. */
  public Parser(java.io.InputStream stream) {
     this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public Parser(java.io.InputStream stream, String encoding) {
    try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source = new ParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 21; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
     ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
    try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 21; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public Parser(java.io.Reader stream) {
    jj_input_stream = new SimpleCharStream(stream, 1, 1);
    token_source = new ParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 21; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 21; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public Parser(ParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 21; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(ParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 21; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }


/** Get the next Token. */
  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      jj_entries_loop: for (java.util.Iterator<?> it = jj_expentries.iterator(); it.hasNext();) {
        int[] oldentry = (int[])(it.next());
        if (oldentry.length == jj_expentry.length) {
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              continue jj_entries_loop;
            }
          }
          jj_expentries.add(jj_expentry);
          break jj_entries_loop;
        }
      }
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  /** Generate ParseException. */
  public ParseException generateParseException() {
    jj_expentries.clear();
    boolean[] la1tokens = new boolean[46];
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 21; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
          if ((jj_la1_1[i] & (1<<j)) != 0) {
            la1tokens[32+j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 46; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.add(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = jj_expentries.get(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

  private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 1; i++) {
    try {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
          }
        }
        p = p.next;
      } while (p != null);
      } catch(LookaheadSuccess ls) { }
    }
    jj_rescan = false;
  }

  private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}
