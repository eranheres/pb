package com.pb.validator;

import com.pb.dao.Snapshot;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by eranh on 12/10/15.
 */
public class SnapshotValidatorValidCardsTest {

    @Test
    public void testValidate() throws Exception {
        Snapshot snapshot= new Snapshot();
        snapshot.getCards();
    }
}