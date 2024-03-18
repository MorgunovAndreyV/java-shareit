package ru.practicum.shareit.comparators;


import ru.practicum.shareit.item.model.Item;

import java.util.Comparator;

public class ItemComparators {

    public static final Comparator<Item> compareItemsById = (item1, item2) -> {
        if (item1.getId() != null && item2.getId() != null) {
            return item1.getId().compareTo(item2.getId());

        } else if (item1.getId() == null && item2.getId() != null) {
            return -1;

        } else if (item1.getId() != null) {
            return 1;

        } else {
            return 0;

        }

    };

}
