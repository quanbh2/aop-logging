package net.friend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TestModel {
    String name;
    int age;
    String address;
    List<String> list;
    TestObject object;
}
