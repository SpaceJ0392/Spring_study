package hellojpa;

import hellojpa.domain.embadded.Address;

public class ValueMain {
    public static void main(String[] args) {
        int a = 10;
        int b = 10;
        System.out.println("a == b: " + (a == b));

        Address address = new Address("city", "street", "10000");
        Address newAddress = new Address("city", "street", "10000");
        System.out.println("address == newAddress: " + (address == newAddress)); //False
        System.out.println("address == newAddress: " + (address.equals(newAddress))); //False -- equals가 기본적으로 == 비교
        //overridng 필요

    }
}
