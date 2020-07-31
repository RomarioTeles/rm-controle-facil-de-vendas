package br.com.rm.cfv.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

public abstract class EditTextMaskUtil {

    public static final String MASK_CPF_CNPJ = "CPF_CNPJ";
    public static final String MASK_CNPJ = "CNPJ";
    public static final String MASK_CPF = "CPF";
    public static final String MASK_DATE = "DATE";
    public static final String MASK_TELEFONE = "TELEFONE";

    public static final Map<String, String> masks;

    static {
        masks = new HashMap<>();
        masks.put("CPF", "###.###.###-##");
        masks.put("CNPJ", "##.###.###/####-##");
        masks.put("DATE", "##/##/####");
        masks.put("TELEFONE", "(##) # ####-####");
        masks.put("CPF_CNPJ", "##############");
    }

    public static String unmask(String s) {
        return s.replaceAll("[^0-9]*", "");
    }

    public static TextWatcher insert(final EditText editText, final String maskType) {
        return new TextWatcher() {
            boolean isUpdating;
            String old = "";

            public void onTextChanged(CharSequence text, int start, int before, int count) {
                String str = EditTextMaskUtil.unmask(text.toString());
                String mask = getDefaultMask(maskType);
                if(MASK_CPF_CNPJ.equals(maskType)){
                    mask = getCpfCnpjMask(str);
                }
                String mascara = "";
                if (isUpdating) {
                    old = str;
                    isUpdating = false;
                    return;
                }
                int i = 0;
                for (char m : mask.toCharArray()) {
                    if ((m != '#' && str.length() > old.length()) || (m != '#' && str.length() < old.length() && str.length() != i)) {
                        mascara += m;
                        continue;
                    }

                    try {
                        mascara += str.charAt(i);
                    } catch (Exception e) {
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                editText.setText(mascara);
                editText.setSelection(mascara.length());
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        };
    }

    private static String getDefaultMask(String str) {
        String defaultMask = masks.get(str);
        return defaultMask == null ? "###################" : defaultMask;
    }

    private static String getCpfCnpjMask(String text){
        String defaultMask = masks.get(MASK_CPF);
        if (text.length() > 11) {
            defaultMask = masks.get(MASK_CNPJ);
        }
        return defaultMask;
    }
}

