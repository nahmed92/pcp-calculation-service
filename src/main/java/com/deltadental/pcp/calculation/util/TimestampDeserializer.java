package com.deltadental.pcp.calculation.util;

import com.deltadental.pcp.calculation.error.PCPCalculationServiceErrors;
import com.deltadental.platform.common.annotation.aop.MethodExecutionTime;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class TimestampDeserializer extends StdDeserializer<Timestamp> {

    private static final long serialVersionUID = 1L;

    public TimestampDeserializer() {
        super(Timestamp.class);
    }

    public TimestampDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    @MethodExecutionTime
    public Timestamp deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        String dateStr = jsonParser.getText();
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSXXX");
            Date date = format.parse(dateStr);
            return new Timestamp(date.getTime());
        } catch (Exception e) {
            log.error("Unable to parse timestamp {} ", dateStr);
            throw PCPCalculationServiceErrors.INVALID_TIMESTAMP_ERROR.createException(e);
        }
    }
}
