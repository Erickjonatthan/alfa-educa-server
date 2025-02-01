package com.projeto.alfaeduca.domain.usuario.email;

import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;

public class EmailVerifier {

    public boolean verificaEmail(String email) {
        // Extrai o domínio do e-mail
        String domain = email.substring(email.indexOf("@") + 1);

        try {
            // Faz a consulta DNS para registros MX
            Lookup lookup = new Lookup(domain, Type.MX);
            lookup.run();

            // Verifica se há registros MX
            if (lookup.getResult() == Lookup.SUCCESSFUL) {
                Record[] records = lookup.getAnswers();
                return records.length > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}