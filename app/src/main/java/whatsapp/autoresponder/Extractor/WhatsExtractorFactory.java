package whatsapp.autoresponder.Extractor;

import android.content.Context;

public class WhatsExtractorFactory {
    public WhatsExtractor getWhatsExtractor(Context context, int apiLevel) {
        if (apiLevel >= 24) {
            return new c(context);
        }
        if (apiLevel >= 21) {
            return new b(context);
        }
        return new a(context);
    }
}
