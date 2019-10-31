package com.thd.opc;

import org.jinterop.dcom.core.JIVariant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JIVariants {

    private static final Logger LOG = LoggerFactory.getLogger(JIVariants.class);

    public static Object getValue(JIVariant originalValue){
        Object value = null;
        if ( originalValue == null ){
            return value;
        }

        try {
            value = originalValue.getObject();

            //        4 -   java.lang.Float
            //        19-   org.jinterop.dcom.core.JIUnsignedInteger
            //        18-   org.jinterop.dcom.core.JIUnsignedShort
            //        11-   java.lang.Boolean


            //            0-org.jinterop.dcom.core.VariantBody$EMPTY
            //            17-org.jinterop.dcom.core.JIUnsignedByte
            //            16-java.lang.Character

            switch ( originalValue.getType()) {
                case 8:
                    value = originalValue.getObjectAsString().getString();
                    break;
                case 16:
                    value = originalValue.getObjectAsChar();
                    break;
                case 11:
                    value = originalValue.getObjectAsBoolean();
                    break;
                case 18:
                case 19:
                    value = originalValue.getObjectAsUnsigned().getValue();
                    break;
                case 4:
                    value = originalValue.getObjectAsFloat();
                    break;
                default:
            }

        } catch (Exception e){
            LOG.error(e.getMessage(), e);
        }

        return value;
    }

}
