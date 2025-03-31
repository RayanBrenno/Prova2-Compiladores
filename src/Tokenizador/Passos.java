package Tokenizador;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class Passos {

    public static ArrayList<String> separarTexto(String code, Map<String, Tokenizer> tabelaDeSimbolos) {

        Map<String, String> dicSimbolos = new HashMap<String, String>() {
            {
                put("=", "SYM_EQUAL");
                put(";", "SYM_PV");
                put("+", "SYM_SUM");
                put("-", "SYM_SUB");
                put("*", "SYM_MULT");
                put("/", "SYM_DIV");
                put("(", "SYM_PARABRE");
                put(")", "SYM_PARFECHA");
                put("{", "SYM_CHAVEABRE");
                put("}", "SYM_CHAVEFECHA");
                put(">", "SYM_MAIOR");
                put("<", "SYM_MENOR");
                put("<=", "SYM_MENORIGUAL");
                put(">=", "SYM_MAIORIGUAL");
                put("==", "SYM_IGUALDADE");
                put("!", "SYM_NEGACAO");
                put("!=", "SYM_DIFERENTE");
            }
        };

        String aux = "";
        int id = 1;
        ArrayList<String> codeSeparado = new ArrayList<>();
        int i = 0;
        while (i < code.length()) {
            String current = String.valueOf(code.charAt(i));

            if (current.equals("\n")) {
                i++;
                continue;
            } else if (dicSimbolos.keySet().contains(current) || current.equals(" ")) {
                if (aux.length() > 0) {
                    codeSeparado.add(aux);
                    if (verificacaoNaoExiste(tabelaDeSimbolos, aux))
                        tabelaDeSimbolos.put(String.valueOf(id++), gerarObjeto(tabelaDeSimbolos, aux));
                }

                if (!current.equals(" ")) {
                    if (i + 1 < code.length()) {
                        String auxRelacionaisGrandes = current + code.charAt(i + 1);
                        if (dicSimbolos.keySet().contains(auxRelacionaisGrandes)) {
                            if (verificacaoNaoExiste(tabelaDeSimbolos, auxRelacionaisGrandes)) {
                                tabelaDeSimbolos.put(String.valueOf(id++), new Tokenizer(auxRelacionaisGrandes, dicSimbolos.get(auxRelacionaisGrandes), "-"));
                            }
                            codeSeparado.add(auxRelacionaisGrandes);
                            i++;
                        } else {
                            if (verificacaoNaoExiste(tabelaDeSimbolos, current)) {
                                tabelaDeSimbolos.put(String.valueOf(id++), new Tokenizer(current, dicSimbolos.get(current), "-"));
                            }
                            codeSeparado.add(current);
                        }
                    } else {
                        if (verificacaoNaoExiste(tabelaDeSimbolos, current)) {
                            tabelaDeSimbolos.put(String.valueOf(id++),
                                    new Tokenizer(current, dicSimbolos.get(current), "-"));
                        }
                        codeSeparado.add(current);
                    }
                }
                aux = "";
            } else {
                aux += current;
            }
            i++;
        }
        return codeSeparado;

    }

    public static Tokenizer gerarObjeto(Map<String, Tokenizer> tabelaDeSimbolos, String lexema) {

        Map<String, String> dicTiposVariaveis = new HashMap<String, String>() {
            {
                put("int", "KW_INT");
                put("char", "KW_CHAR");
            }
        };

        if (isNumeric(lexema))
            return new Tokenizer(lexema, "NUMBER", lexema);
        else if (dicTiposVariaveis.containsKey(lexema))
            return new Tokenizer(lexema, dicTiposVariaveis.get(lexema), "-");
        else
            return new Tokenizer(lexema, "ID", "-");
    }

    public static String[] tokenizarTexto(Map<String, Tokenizer> tabelaDeSimbolos, ArrayList<String> tokenizar) {

        String[] tokenizado = new String[tabelaDeSimbolos.size()];
        for (int i = 0; i < tabelaDeSimbolos.size(); i++)
            tokenizado[i] = "<" + tabelaDeSimbolos.get((i + 1) + "").getToken() + ", " + (i + 1) + ">";

        String[] textoTokenizado = new String[tokenizar.size()];
        for (int i = 0; i < tokenizar.size(); i++)
            textoTokenizado[i] = tokenizado[(encontrarId(tabelaDeSimbolos, tokenizar.get(i))) - 1];

        return textoTokenizado;

    }

    public static Integer encontrarId(Map<String, Tokenizer> tabelaDeSimbolos, String lexemaProcurado) {
        int id = 0;
        for (Map.Entry<String, Tokenizer> current : tabelaDeSimbolos.entrySet()) {
            if (current.getValue().getLexema().equals(lexemaProcurado)) {
                id = Integer.parseInt(current.getKey());
                break;
            }
        }
        return id;
    }

    public static Boolean verificacaoNaoExiste(Map<String, Tokenizer> dic, String lexema) {
        for (Map.Entry<String, Tokenizer> aux : dic.entrySet()) {
            Tokenizer token = aux.getValue();
            if (token.lexema.equals(lexema)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}