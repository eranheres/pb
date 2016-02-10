package com.pb.validator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.pb.dao.Snapshot;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;


/**
 */
public class SnapshotValidatorValidValuesTest {

    @Test
    public void testIsValid() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // Test valid values
        URL url = Thread.currentThread().getContextClassLoader().getResource("SnapshotValidatorValidValuesTest_Valid.json");
        Assert.assertNotNull(url);
        Snapshot[] lu = mapper.readValue(new File(url.getPath()), Snapshot[].class);

        SnapshotValidator validator = new SnapshotValidatorValidValues();

        assertEquals(validator.validate(lu[0]), ValidatorStatus.OK);
        assertEquals(validator.validate(lu[1]), ValidatorStatus.OK);
        assertEquals(validator.validate(lu[4]), ValidatorStatus.OK);
        assertEquals(validator.validate(lu[5]), ValidatorStatus.OK);
        assertEquals(validator.validate(lu[6]), ValidatorStatus.OK);
        assertEquals(8, lu.length); // Make sure nothing is missed

        // Test invalid values
        url = Thread.currentThread().getContextClassLoader().getResource("SnapshotValidatorValidValuesTest_Invalid.json");
        Assert.assertNotNull(url);
        lu = mapper.readValue(new File(url.getPath()), Snapshot[].class);

        assertEquals(SnapshotValidatorValidValues.INVALID_FIELD_DATATYPE, validator.validate(lu[0]));
        assertEquals(SnapshotValidatorValidValues.INVALID_FIELD_ACTION, validator.validate(lu[1]));
        assertTrue(lu.length == 2); // Make sure nothing is missed
    }

    private final static Map<String, Double> mandatorySymbols = ImmutableMap.of(
            Snapshot.SYMBOLS.AMOUNT_TO_CALL,      0.0,
            Snapshot.SYMBOLS.BALANCE,             0.0,
            Snapshot.SYMBOLS.BIG_BLIND,           0.0,
            Snapshot.SYMBOLS.OPPONENTS_WITH_HIGHER_STACK, 0.0,
            Snapshot.SYMBOLS.PREVACTION, 0.0
    );

    @DataProvider(name = "Parameters")
    public Object[][] parametersForTestNegativeOrNullValues() {
        Object values[] = mandatorySymbols.keySet().toArray();
        return new Object[][] {
            { values[0] }, { values[1] }, { values[2] }, { values[3] }
        };
    }

    @Test(dataProvider = "Parameters")
    public void testNegativeOrNullValues(String str) throws Exception {
        SnapshotValidator validator = new SnapshotValidatorValidValues();
        Snapshot snapshot = new Snapshot();
        snapshot.setSymbols(new HashMap<>(mandatorySymbols));
        snapshot.setState(new Snapshot.State());
        snapshot.getState().setDatatype(Snapshot.VALUES.DATATYPE_HANDRESET);

        // Validate 0 value
        assertEquals(validator.validate(snapshot), ValidatorStatus.OK);

        // Validate positive value
        snapshot.getSymbols().put(str, 10.0);
        assertEquals(validator.validate(snapshot), ValidatorStatus.OK);

        // Validate negative value
        snapshot.getSymbols().put(str, -10.0);
        assertEquals(validator.validate(snapshot), SnapshotValidatorValidValues.NULL_OR_NEGATIVE_SYMBOL);

        // Validate null value
        snapshot.getSymbols().remove(str);
        assertEquals(validator.validate(snapshot), SnapshotValidatorValidValues.NULL_OR_NEGATIVE_SYMBOL);
    }

    @DataProvider(name = "prevactions")
    public Object[][] parametersForPrevactionTest() {
        return new Object[][] {
                { ValidatorStatus.OK,                               Snapshot.VALUES.PREVACTION_PREFOLD},
                { ValidatorStatus.OK,                               Snapshot.VALUES.PREVACTION_FOLD},
                { ValidatorStatus.OK,                               Snapshot.VALUES.PREVACTION_CHECK},
                { ValidatorStatus.OK,                               Snapshot.VALUES.PREVACTION_CALL},
                { SnapshotValidatorValidValues.VALUE_OUT_OF_BOUNDS, Snapshot.VALUES.PREVACTION_RAISE},
                { ValidatorStatus.OK,                               Snapshot.VALUES.PREVACTION_BETRAISE},
                { ValidatorStatus.OK,                               Snapshot.VALUES.PREVACTION_ALLIN},
                { SnapshotValidatorValidValues.VALUE_OUT_OF_BOUNDS, Snapshot.VALUES.PREVACTION_ALLIN + 1},
                { SnapshotValidatorValidValues.VALUE_OUT_OF_BOUNDS, Snapshot.VALUES.PREVACTION_PREFOLD -1}
        };
    }
    @Test(dataProvider = "prevactions")
    public void testPrevactionValues(ValidatorStatus expected, double prevaction) {
        SnapshotValidator validator = new SnapshotValidatorValidValues();
        Snapshot snapshot = new Snapshot();
        snapshot.setSymbols(new HashMap<>(mandatorySymbols));
        snapshot.setState(new Snapshot.State());
        snapshot.getState().setDatatype(Snapshot.VALUES.DATATYPE_HANDRESET);

        // Validate 0 value
        snapshot.getSymbols().put(Snapshot.SYMBOLS.PREVACTION, prevaction);
        assertEquals(validator.validate(snapshot), expected);
    }

}