package io.tapas;

import lombok.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Yoon
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class IntegerTriple {
    private int value0;
    private int value1;
    private int value2;

    public IntegerTriple(Object[] objects) {
        if (objects != null && objects.length >= 3) {
            value0 = objects[0] == null ? 0 : (Integer) objects[0];
            value1 = objects[1] == null ? 0 : (Integer) objects[1];
            value2 = objects[2] == null ? 0 : (Integer) objects[2];
        }
    }

    public static IntegerTriple createFromLongs(Object[] objects) {
        if (objects != null && objects.length >= 3) {
            List<Integer> values = Arrays.stream(objects).map(o -> {
                if (o != null) {
                    return ((Long) o).intValue();
                }
                return 0;
            }).collect(Collectors.toList());
            return new IntegerTriple(values.get(0), values.get(1), values.get(2));
        }
        return new IntegerTriple();
    }
}
