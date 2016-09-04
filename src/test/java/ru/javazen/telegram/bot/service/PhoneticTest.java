package ru.javazen.telegram.bot.service;


import net.sf.junidecode.Junidecode;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.language.*;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@Ignore
@RunWith(Parameterized.class)
public class PhoneticTest {
    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        Soundex soundex =  new Soundex();
        DaitchMokotoffSoundex daitchMokotoffSoundex = new DaitchMokotoffSoundex();
        RefinedSoundex refinedSoundex = new RefinedSoundex();
        Nysiis nysiis = new Nysiis();
        Metaphone metaphone = new Metaphone();
        metaphone.setMaxCodeLen(50);
        DoubleMetaphone doubleMetaphone = new DoubleMetaphone();
        doubleMetaphone.setMaxCodeLen(50);
        MatchRatingApproachEncoder matchRatingApproachEncoder = new MatchRatingApproachEncoder();
        Caverphone1 caverphone1 = new Caverphone1();
        Caverphone2 caverphone2 = new Caverphone2();
        ColognePhonetic colognePhonetic = new ColognePhonetic();

        return Arrays.asList(
                new Object[]{soundex},
                new Object[]{refinedSoundex},
                new Object[]{daitchMokotoffSoundex},
                new Object[]{nysiis},
                new Object[]{metaphone},
                new Object[]{doubleMetaphone},
                new Object[]{matchRatingApproachEncoder},
                new Object[]{caverphone1},
                new Object[]{caverphone2},
                new Object[]{colognePhonetic}
        );
    }

    private StringEncoder encoder;

    public PhoneticTest(StringEncoder encoder) {
        this.encoder = encoder;
    }

    @Test
    public void test() throws Exception {
        String s1 = Junidecode.unidecode("А может быть забудем всё и сбежим?");
        String s2 = Junidecode.unidecode("Аможит быт зобудим все и збежым?");
        System.out.format("%30s: %s\n%32s%s\n",
                encoder.getClass().getSimpleName(),
                encoder.encode(s1),
                " ",
                encoder.encode(s2));

    }
}
