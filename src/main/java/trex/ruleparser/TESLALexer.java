package trex.ruleparser;// Generated from TESLA.g4 by ANTLR 4.5.3
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class TESLALexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, ASSIGN=17, 
		DEFINE=18, FROM=19, WHERE=20, CONSUMING=21, VALTYPE=22, SEL_POLICY=23, 
		AGGR_FUN=24, OPERATOR=25, LOGICAL_OPERATOR=26, BINOP_MUL=27, BINOP_ADD=28, 
		SPATIAL_OP=29, INT_VAL=30, FLOAT_VAL=31, BOOL_VAL=32, STRING_VAL=33, GEOMETRY_VAL=34, 
		EVT_NAME=35, ATTR_NAME=36, PARAM_NAME=37, WS=38;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "ASSIGN", 
		"DEFINE", "FROM", "WHERE", "CONSUMING", "VALTYPE", "SEL_POLICY", "AGGR_FUN", 
		"OPERATOR", "LOGICAL_OPERATOR", "BINOP_MUL", "BINOP_ADD", "SPATIAL_OP", 
		"INT_VAL", "FLOAT_VAL", "BOOL_VAL", "STRING_VAL", "GEOMETRY_VAL", "EVT_NAME", 
		"ATTR_NAME", "PARAM_NAME", "WS"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'.'", "'=>'", "'within'", "'from'", "'between'", "'and'", "'('", 
		"','", "')'", "':'", "':='", "'['", "']'", "'as'", "'not'", "';'", "'Assign'", 
		"'Define'", "'From'", "'Where'", "'Consuming'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, "ASSIGN", "DEFINE", "FROM", "WHERE", "CONSUMING", 
		"VALTYPE", "SEL_POLICY", "AGGR_FUN", "OPERATOR", "LOGICAL_OPERATOR", "BINOP_MUL", 
		"BINOP_ADD", "SPATIAL_OP", "INT_VAL", "FLOAT_VAL", "BOOL_VAL", "STRING_VAL", 
		"GEOMETRY_VAL", "EVT_NAME", "ATTR_NAME", "PARAM_NAME", "WS"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	StringBuilder buf = new StringBuilder(); // can't make locals in lexer rules


	public TESLALexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "TESLA.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 32:
			STRING_VAL_action((RuleContext)_localctx, actionIndex);
			break;
		case 33:
			GEOMETRY_VAL_action((RuleContext)_localctx, actionIndex);
			break;
		}
	}
	private void STRING_VAL_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0:
			buf.append('\r');
			break;
		case 1:
			buf.append('\n');
			break;
		case 2:
			buf.append('\t');
			break;
		case 3:
			buf.append('\\');
			break;
		case 4:
			buf.append('"');
			break;
		case 5:
			buf.append((char)_input.LA(-1));
			break;
		case 6:
			setText(buf.toString()); buf.setLength(0); System.out.println(getText());
			break;
		}
	}
	private void GEOMETRY_VAL_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 7:
			buf.append('\r');
			break;
		case 8:
			buf.append('\n');
			break;
		case 9:
			buf.append('\t');
			break;
		case 10:
			buf.append('\\');
			break;
		case 11:
			buf.append('"');
			break;
		case 12:
			buf.append((char)_input.LA(-1));
			break;
		case 13:
			setText(buf.toString()); buf.setLength(0); System.out.println(getText());
			break;
		}
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2(\u01b7\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\3\2\3\2\3\3\3\3\3\3\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6"+
		"\3\6\3\7\3\7\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\f\3\r"+
		"\3\r\3\16\3\16\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\21\3\21\3\22\3\22"+
		"\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\24\3\24"+
		"\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26"+
		"\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27"+
		"\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27"+
		"\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\27\5\27\u00c9\n\27\3\30"+
		"\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\5\30\u00d8"+
		"\n\30\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31"+
		"\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\5\31\u00f1\n\31\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\5\32\u0121\n\32\3\33\3\33\3\33\3\33\3\33\5\33\u0128\n"+
		"\33\3\34\3\34\3\35\3\35\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3"+
		"\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3"+
		"\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\3\36\5\36\u014e\n\36\3\37\6\37"+
		"\u0151\n\37\r\37\16\37\u0152\3 \6 \u0156\n \r \16 \u0157\3 \3 \6 \u015c"+
		"\n \r \16 \u015d\3!\3!\3!\3!\3!\3!\3!\3!\3!\5!\u0169\n!\3\"\3\"\3\"\3"+
		"\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\3\"\5\"\u0177\n\"\3\"\3\"\7\"\u017b\n\""+
		"\f\"\16\"\u017e\13\"\3\"\3\"\3\"\3#\3#\3#\3#\3#\3#\3#\3#\3#\3#\3#\3#\5"+
		"#\u018f\n#\3#\3#\7#\u0193\n#\f#\16#\u0196\13#\3#\3#\3#\3$\3$\7$\u019d"+
		"\n$\f$\16$\u01a0\13$\3%\3%\7%\u01a4\n%\f%\16%\u01a7\13%\3&\3&\3&\7&\u01ac"+
		"\n&\f&\16&\u01af\13&\3\'\6\'\u01b2\n\'\r\'\16\'\u01b3\3\'\3\'\2\2(\3\3"+
		"\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21"+
		"!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!"+
		"A\"C#E$G%I&K\'M(\3\2\b\4\2((~~\4\2,,\61\61\4\2--//\4\2$$^^\6\2\62;C\\"+
		"aac|\5\2\13\f\17\17\"\"\u01e1\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t"+
		"\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2"+
		"\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2"+
		"\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2"+
		"+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2"+
		"\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2"+
		"C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\3O\3"+
		"\2\2\2\5Q\3\2\2\2\7T\3\2\2\2\t[\3\2\2\2\13`\3\2\2\2\rh\3\2\2\2\17l\3\2"+
		"\2\2\21n\3\2\2\2\23p\3\2\2\2\25r\3\2\2\2\27t\3\2\2\2\31w\3\2\2\2\33y\3"+
		"\2\2\2\35{\3\2\2\2\37~\3\2\2\2!\u0082\3\2\2\2#\u0084\3\2\2\2%\u008b\3"+
		"\2\2\2\'\u0092\3\2\2\2)\u0097\3\2\2\2+\u009d\3\2\2\2-\u00c8\3\2\2\2/\u00d7"+
		"\3\2\2\2\61\u00f0\3\2\2\2\63\u0120\3\2\2\2\65\u0127\3\2\2\2\67\u0129\3"+
		"\2\2\29\u012b\3\2\2\2;\u014d\3\2\2\2=\u0150\3\2\2\2?\u0155\3\2\2\2A\u0168"+
		"\3\2\2\2C\u016a\3\2\2\2E\u0182\3\2\2\2G\u019a\3\2\2\2I\u01a1\3\2\2\2K"+
		"\u01a8\3\2\2\2M\u01b1\3\2\2\2OP\7\60\2\2P\4\3\2\2\2QR\7?\2\2RS\7@\2\2"+
		"S\6\3\2\2\2TU\7y\2\2UV\7k\2\2VW\7v\2\2WX\7j\2\2XY\7k\2\2YZ\7p\2\2Z\b\3"+
		"\2\2\2[\\\7h\2\2\\]\7t\2\2]^\7q\2\2^_\7o\2\2_\n\3\2\2\2`a\7d\2\2ab\7g"+
		"\2\2bc\7v\2\2cd\7y\2\2de\7g\2\2ef\7g\2\2fg\7p\2\2g\f\3\2\2\2hi\7c\2\2"+
		"ij\7p\2\2jk\7f\2\2k\16\3\2\2\2lm\7*\2\2m\20\3\2\2\2no\7.\2\2o\22\3\2\2"+
		"\2pq\7+\2\2q\24\3\2\2\2rs\7<\2\2s\26\3\2\2\2tu\7<\2\2uv\7?\2\2v\30\3\2"+
		"\2\2wx\7]\2\2x\32\3\2\2\2yz\7_\2\2z\34\3\2\2\2{|\7c\2\2|}\7u\2\2}\36\3"+
		"\2\2\2~\177\7p\2\2\177\u0080\7q\2\2\u0080\u0081\7v\2\2\u0081 \3\2\2\2"+
		"\u0082\u0083\7=\2\2\u0083\"\3\2\2\2\u0084\u0085\7C\2\2\u0085\u0086\7u"+
		"\2\2\u0086\u0087\7u\2\2\u0087\u0088\7k\2\2\u0088\u0089\7i\2\2\u0089\u008a"+
		"\7p\2\2\u008a$\3\2\2\2\u008b\u008c\7F\2\2\u008c\u008d\7g\2\2\u008d\u008e"+
		"\7h\2\2\u008e\u008f\7k\2\2\u008f\u0090\7p\2\2\u0090\u0091\7g\2\2\u0091"+
		"&\3\2\2\2\u0092\u0093\7H\2\2\u0093\u0094\7t\2\2\u0094\u0095\7q\2\2\u0095"+
		"\u0096\7o\2\2\u0096(\3\2\2\2\u0097\u0098\7Y\2\2\u0098\u0099\7j\2\2\u0099"+
		"\u009a\7g\2\2\u009a\u009b\7t\2\2\u009b\u009c\7g\2\2\u009c*\3\2\2\2\u009d"+
		"\u009e\7E\2\2\u009e\u009f\7q\2\2\u009f\u00a0\7p\2\2\u00a0\u00a1\7u\2\2"+
		"\u00a1\u00a2\7w\2\2\u00a2\u00a3\7o\2\2\u00a3\u00a4\7k\2\2\u00a4\u00a5"+
		"\7p\2\2\u00a5\u00a6\7i\2\2\u00a6,\3\2\2\2\u00a7\u00a8\7U\2\2\u00a8\u00a9"+
		"\7v\2\2\u00a9\u00aa\7t\2\2\u00aa\u00ab\7k\2\2\u00ab\u00ac\7p\2\2\u00ac"+
		"\u00c9\7i\2\2\u00ad\u00ae\7K\2\2\u00ae\u00af\7p\2\2\u00af\u00c9\7v\2\2"+
		"\u00b0\u00b1\7H\2\2\u00b1\u00b2\7n\2\2\u00b2\u00b3\7q\2\2\u00b3\u00b4"+
		"\7c\2\2\u00b4\u00c9\7v\2\2\u00b5\u00b6\7D\2\2\u00b6\u00b7\7q\2\2\u00b7"+
		"\u00b8\7q\2\2\u00b8\u00c9\7n\2\2\u00b9\u00ba\7S\2\2\u00ba\u00bb\7w\2\2"+
		"\u00bb\u00bc\7c\2\2\u00bc\u00bd\7n\2\2\u00bd\u00be\7k\2\2\u00be\u00bf"+
		"\7v\2\2\u00bf\u00c9\7{\2\2\u00c0\u00c1\7I\2\2\u00c1\u00c2\7g\2\2\u00c2"+
		"\u00c3\7q\2\2\u00c3\u00c4\7o\2\2\u00c4\u00c5\7g\2\2\u00c5\u00c6\7v\2\2"+
		"\u00c6\u00c7\7t\2\2\u00c7\u00c9\7{\2\2\u00c8\u00a7\3\2\2\2\u00c8\u00ad"+
		"\3\2\2\2\u00c8\u00b0\3\2\2\2\u00c8\u00b5\3\2\2\2\u00c8\u00b9\3\2\2\2\u00c8"+
		"\u00c0\3\2\2\2\u00c9.\3\2\2\2\u00ca\u00cb\7G\2\2\u00cb\u00cc\7c\2\2\u00cc"+
		"\u00cd\7e\2\2\u00cd\u00d8\7j\2\2\u00ce\u00cf\7N\2\2\u00cf\u00d0\7c\2\2"+
		"\u00d0\u00d1\7u\2\2\u00d1\u00d8\7v\2\2\u00d2\u00d3\7H\2\2\u00d3\u00d4"+
		"\7k\2\2\u00d4\u00d5\7t\2\2\u00d5\u00d6\7u\2\2\u00d6\u00d8\7v\2\2\u00d7"+
		"\u00ca\3\2\2\2\u00d7\u00ce\3\2\2\2\u00d7\u00d2\3\2\2\2\u00d8\60\3\2\2"+
		"\2\u00d9\u00da\7C\2\2\u00da\u00db\7X\2\2\u00db\u00f1\7I\2\2\u00dc\u00dd"+
		"\7U\2\2\u00dd\u00de\7W\2\2\u00de\u00f1\7O\2\2\u00df\u00e0\7O\2\2\u00e0"+
		"\u00e1\7C\2\2\u00e1\u00f1\7Z\2\2\u00e2\u00e3\7O\2\2\u00e3\u00e4\7K\2\2"+
		"\u00e4\u00f1\7P\2\2\u00e5\u00e6\7E\2\2\u00e6\u00e7\7Q\2\2\u00e7\u00e8"+
		"\7W\2\2\u00e8\u00e9\7P\2\2\u00e9\u00f1\7V\2\2\u00ea\u00eb\7E\2\2\u00eb"+
		"\u00ec\7G\2\2\u00ec\u00ed\7P\2\2\u00ed\u00ee\7V\2\2\u00ee\u00ef\7G\2\2"+
		"\u00ef\u00f1\7T\2\2\u00f0\u00d9\3\2\2\2\u00f0\u00dc\3\2\2\2\u00f0\u00df"+
		"\3\2\2\2\u00f0\u00e2\3\2\2\2\u00f0\u00e5\3\2\2\2\u00f0\u00ea\3\2\2\2\u00f1"+
		"\62\3\2\2\2\u00f2\u0121\4>@\2\u00f3\u00f4\7@\2\2\u00f4\u0121\7?\2\2\u00f5"+
		"\u00f6\7>\2\2\u00f6\u0121\7?\2\2\u00f7\u00f8\7#\2\2\u00f8\u0121\7?\2\2"+
		"\u00f9\u0121\t\2\2\2\u00fa\u00fb\7K\2\2\u00fb\u00fc\7P\2\2\u00fc\u00fd"+
		"\7V\2\2\u00fd\u00fe\7G\2\2\u00fe\u00ff\7T\2\2\u00ff\u0100\7U\2\2\u0100"+
		"\u0101\7G\2\2\u0101\u0102\7E\2\2\u0102\u0121\7V\2\2\u0103\u0104\7E\2\2"+
		"\u0104\u0105\7Q\2\2\u0105\u0106\7P\2\2\u0106\u0107\7V\2\2\u0107\u0108"+
		"\7C\2\2\u0108\u0109\7K\2\2\u0109\u010a\7P\2\2\u010a\u0121\7U\2\2\u010b"+
		"\u010c\7F\2\2\u010c\u010d\7K\2\2\u010d\u010e\7U\2\2\u010e\u010f\7V\2\2"+
		"\u010f\u0110\7C\2\2\u0110\u0111\7P\2\2\u0111\u0112\7E\2\2\u0112\u0121"+
		"\7G\2\2\u0113\u0114\7K\2\2\u0114\u0115\7P\2\2\u0115\u0116\7V\2\2\u0116"+
		"\u0117\7G\2\2\u0117\u0118\7T\2\2\u0118\u0119\7K\2\2\u0119\u011a\7Q\2\2"+
		"\u011a\u011b\7T\2\2\u011b\u011c\7R\2\2\u011c\u011d\7Q\2\2\u011d\u011e"+
		"\7K\2\2\u011e\u011f\7P\2\2\u011f\u0121\7V\2\2\u0120\u00f2\3\2\2\2\u0120"+
		"\u00f3\3\2\2\2\u0120\u00f5\3\2\2\2\u0120\u00f7\3\2\2\2\u0120\u00f9\3\2"+
		"\2\2\u0120\u00fa\3\2\2\2\u0120\u0103\3\2\2\2\u0120\u010b\3\2\2\2\u0120"+
		"\u0113\3\2\2\2\u0121\64\3\2\2\2\u0122\u0123\7C\2\2\u0123\u0124\7p\2\2"+
		"\u0124\u0128\7f\2\2\u0125\u0126\7Q\2\2\u0126\u0128\7t\2\2\u0127\u0122"+
		"\3\2\2\2\u0127\u0125\3\2\2\2\u0128\66\3\2\2\2\u0129\u012a\t\3\2\2\u012a"+
		"8\3\2\2\2\u012b\u012c\t\4\2\2\u012c:\3\2\2\2\u012d\u012e\7K\2\2\u012e"+
		"\u012f\7P\2\2\u012f\u0130\7V\2\2\u0130\u0131\7G\2\2\u0131\u0132\7T\2\2"+
		"\u0132\u0133\7U\2\2\u0133\u0134\7G\2\2\u0134\u0135\7E\2\2\u0135\u0136"+
		"\7V\2\2\u0136\u0137\7K\2\2\u0137\u0138\7Q\2\2\u0138\u014e\7P\2\2\u0139"+
		"\u013a\7E\2\2\u013a\u013b\7G\2\2\u013b\u013c\7P\2\2\u013c\u013d\7V\2\2"+
		"\u013d\u013e\7T\2\2\u013e\u013f\7Q\2\2\u013f\u0140\7K\2\2\u0140\u014e"+
		"\7F\2\2\u0141\u0142\7F\2\2\u0142\u0143\7K\2\2\u0143\u0144\7U\2\2\u0144"+
		"\u0145\7V\2\2\u0145\u0146\7C\2\2\u0146\u0147\7P\2\2\u0147\u0148\7E\2\2"+
		"\u0148\u0149\7G\2\2\u0149\u014a\7H\2\2\u014a\u014b\7T\2\2\u014b\u014c"+
		"\7Q\2\2\u014c\u014e\7O\2\2\u014d\u012d\3\2\2\2\u014d\u0139\3\2\2\2\u014d"+
		"\u0141\3\2\2\2\u014e<\3\2\2\2\u014f\u0151\4\62;\2\u0150\u014f\3\2\2\2"+
		"\u0151\u0152\3\2\2\2\u0152\u0150\3\2\2\2\u0152\u0153\3\2\2\2\u0153>\3"+
		"\2\2\2\u0154\u0156\4\62;\2\u0155\u0154\3\2\2\2\u0156\u0157\3\2\2\2\u0157"+
		"\u0155\3\2\2\2\u0157\u0158\3\2\2\2\u0158\u0159\3\2\2\2\u0159\u015b\7\60"+
		"\2\2\u015a\u015c\4\62;\2\u015b\u015a\3\2\2\2\u015c\u015d\3\2\2\2\u015d"+
		"\u015b\3\2\2\2\u015d\u015e\3\2\2\2\u015e@\3\2\2\2\u015f\u0160\7H\2\2\u0160"+
		"\u0161\7c\2\2\u0161\u0162\7n\2\2\u0162\u0163\7u\2\2\u0163\u0169\7g\2\2"+
		"\u0164\u0165\7V\2\2\u0165\u0166\7t\2\2\u0166\u0167\7w\2\2\u0167\u0169"+
		"\7g\2\2\u0168\u015f\3\2\2\2\u0168\u0164\3\2\2\2\u0169B\3\2\2\2\u016a\u017c"+
		"\7$\2\2\u016b\u0176\7^\2\2\u016c\u016d\7t\2\2\u016d\u0177\b\"\2\2\u016e"+
		"\u016f\7p\2\2\u016f\u0177\b\"\3\2\u0170\u0171\7v\2\2\u0171\u0177\b\"\4"+
		"\2\u0172\u0173\7^\2\2\u0173\u0177\b\"\5\2\u0174\u0175\7$\2\2\u0175\u0177"+
		"\b\"\6\2\u0176\u016c\3\2\2\2\u0176\u016e\3\2\2\2\u0176\u0170\3\2\2\2\u0176"+
		"\u0172\3\2\2\2\u0176\u0174\3\2\2\2\u0177\u017b\3\2\2\2\u0178\u0179\n\5"+
		"\2\2\u0179\u017b\b\"\7\2\u017a\u016b\3\2\2\2\u017a\u0178\3\2\2\2\u017b"+
		"\u017e\3\2\2\2\u017c\u017a\3\2\2\2\u017c\u017d\3\2\2\2\u017d\u017f\3\2"+
		"\2\2\u017e\u017c\3\2\2\2\u017f\u0180\7$\2\2\u0180\u0181\b\"\b\2\u0181"+
		"D\3\2\2\2\u0182\u0194\7$\2\2\u0183\u018e\7^\2\2\u0184\u0185\7t\2\2\u0185"+
		"\u018f\b#\t\2\u0186\u0187\7p\2\2\u0187\u018f\b#\n\2\u0188\u0189\7v\2\2"+
		"\u0189\u018f\b#\13\2\u018a\u018b\7^\2\2\u018b\u018f\b#\f\2\u018c\u018d"+
		"\7$\2\2\u018d\u018f\b#\r\2\u018e\u0184\3\2\2\2\u018e\u0186\3\2\2\2\u018e"+
		"\u0188\3\2\2\2\u018e\u018a\3\2\2\2\u018e\u018c\3\2\2\2\u018f\u0193\3\2"+
		"\2\2\u0190\u0191\n\5\2\2\u0191\u0193\b#\16\2\u0192\u0183\3\2\2\2\u0192"+
		"\u0190\3\2\2\2\u0193\u0196\3\2\2\2\u0194\u0192\3\2\2\2\u0194\u0195\3\2"+
		"\2\2\u0195\u0197\3\2\2\2\u0196\u0194\3\2\2\2\u0197\u0198\7$\2\2\u0198"+
		"\u0199\b#\17\2\u0199F\3\2\2\2\u019a\u019e\4C\\\2\u019b\u019d\t\6\2\2\u019c"+
		"\u019b\3\2\2\2\u019d\u01a0\3\2\2\2\u019e\u019c\3\2\2\2\u019e\u019f\3\2"+
		"\2\2\u019fH\3\2\2\2\u01a0\u019e\3\2\2\2\u01a1\u01a5\4c|\2\u01a2\u01a4"+
		"\t\6\2\2\u01a3\u01a2\3\2\2\2\u01a4\u01a7\3\2\2\2\u01a5\u01a3\3\2\2\2\u01a5"+
		"\u01a6\3\2\2\2\u01a6J\3\2\2\2\u01a7\u01a5\3\2\2\2\u01a8\u01a9\7&\2\2\u01a9"+
		"\u01ad\4c|\2\u01aa\u01ac\t\6\2\2\u01ab\u01aa\3\2\2\2\u01ac\u01af\3\2\2"+
		"\2\u01ad\u01ab\3\2\2\2\u01ad\u01ae\3\2\2\2\u01aeL\3\2\2\2\u01af\u01ad"+
		"\3\2\2\2\u01b0\u01b2\t\7\2\2\u01b1\u01b0\3\2\2\2\u01b2\u01b3\3\2\2\2\u01b3"+
		"\u01b1\3\2\2\2\u01b3\u01b4\3\2\2\2\u01b4\u01b5\3\2\2\2\u01b5\u01b6\b\'"+
		"\20\2\u01b6N\3\2\2\2\32\2\u00c8\u00d7\u00f0\u0120\u0127\u014d\u0152\u0157"+
		"\u015d\u0168\u0176\u017a\u017c\u018e\u0192\u0194\u019c\u019e\u01a3\u01a5"+
		"\u01ab\u01ad\u01b3\21\3\"\2\3\"\3\3\"\4\3\"\5\3\"\6\3\"\7\3\"\b\3#\t\3"+
		"#\n\3#\13\3#\f\3#\r\3#\16\3#\17\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}