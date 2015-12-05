package com.pb.validator;

import com.pb.dao.Hand;
import com.pb.dao.Snapshot;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ValidatorsManagerTest {

    @Test
    public void testValidateHand() throws Exception {
        ValidatorsFactory factory = mock(ValidatorsFactory.class);

        HandValidator handValidator1 = mock(HandValidator.class);
        HandValidator handValidator2 = mock(HandValidator.class);
        SnapshotValidator snapshotValidator1 = mock(SnapshotValidator.class);
        SnapshotValidator snapshotValidator2 = mock(SnapshotValidator.class);
        when(factory.getHandValidators()).thenReturn(new HandValidator[]{ handValidator1, handValidator2});
        when(factory.getSnapshotValidators()).thenReturn(new SnapshotValidator[]{ snapshotValidator1, snapshotValidator2 });

        ValidatorsManager manager = new ValidatorsManager(factory);

        // empty hand
        assertEquals(HandValidator.EMPTY_HAND, manager.validateHand(null));
        assertEquals(HandValidator.EMPTY_HAND, manager.validateHand(new Hand()));

        // all are OK
        Snapshot snapshot1 = new Snapshot();
        Snapshot snapshot2 = new Snapshot();
        Hand hand = new Hand(new Snapshot[]{ snapshot1, snapshot2});
        when(handValidator1.validate(hand)).thenReturn(ValidatorStatus.OK);
        when(handValidator2.validate(hand)).thenReturn(ValidatorStatus.OK);
        when(snapshotValidator1.validate(snapshot1)).thenReturn(ValidatorStatus.OK);
        when(snapshotValidator1.validate(snapshot2)).thenReturn(ValidatorStatus.OK);
        when(snapshotValidator2.validate(snapshot1)).thenReturn(ValidatorStatus.OK);
        when(snapshotValidator2.validate(snapshot2)).thenReturn(ValidatorStatus.OK);
        assertEquals(ValidatorStatus.OK, manager.validateHand(hand));

        // failed on hand validator
        when(handValidator2.validate(hand)).thenReturn(HandValidatorTurnSeq.BETROUND_OUT_OF_ORDER);
        assertEquals(HandValidatorTurnSeq.BETROUND_OUT_OF_ORDER, manager.validateHand(hand));

        // failed on snapshot validator
        when(handValidator2.validate(hand)).thenReturn(ValidatorStatus.OK);
        when(snapshotValidator2.validate(snapshot2)).thenReturn(SnapshotValidatorValidValues.INVALID_FIELD_ACTION);
        assertEquals(SnapshotValidatorValidValues.INVALID_FIELD_ACTION, manager.validateHand(hand));
    }

    @Test
    public void testValidateSnapshot() throws Exception {
        ValidatorsFactory factory = mock(ValidatorsFactory.class);

        SnapshotValidator snapshotValidator1 = mock(SnapshotValidator.class);
        SnapshotValidator snapshotValidator2 = mock(SnapshotValidator.class);
        when(factory.getHandValidators()).thenReturn(null);
        when(factory.getSnapshotValidators()).thenReturn(new SnapshotValidator[]{snapshotValidator1, snapshotValidator2});

        Snapshot snapshot = new Snapshot();
        when(snapshotValidator1.validate(snapshot)).thenReturn(ValidatorStatus.OK);
        when(snapshotValidator2.validate(snapshot)).thenReturn(ValidatorStatus.OK);

        ValidatorsManager manager = new ValidatorsManager(factory);

        // Null
        assertEquals(SnapshotValidator.SNAPSHOT_EMPTY, manager.validateSnapshot(null));

        // All is OK
        assertEquals(ValidatorStatus.OK, manager.validateSnapshot(snapshot));

        // Error
        when(snapshotValidator2.validate(snapshot)).thenReturn(SnapshotValidatorValidValues.INVALID_FIELD_ACTION);
        assertEquals(SnapshotValidatorValidValues.INVALID_FIELD_ACTION, manager.validateSnapshot(snapshot));
    }
}
