package ch.idsia.utils;

public class IBLInstance {

    private String instanceGroup;
    private String attributeArray[];

    public IBLInstance(String instance, String instanceGroup) {
        this.instanceGroup = instanceGroup;
        this.attributeArray = instance.split(", ");
    }

    public String[] getAttributeArray() {
        return this.attributeArray;
    }

    public String getInstanceGroup() {
        return this.instanceGroup;
    }

    public String[] setAttributeArray(String[] newattributeArray) {
        this.attributeArray = newattributeArray;
        return this.attributeArray;
    }

    public String setInstanceGroup(String newInstanceGroup) {
        this.instanceGroup = newInstanceGroup;
        return this.instanceGroup;
    }

    public String toString() {
        String result = "instanceGroup: " + this.instanceGroup + "\nattributeArray: [";
        for (String anAttributeArray : this.attributeArray) {
            result = result.concat("'" + anAttributeArray + "', ");
        }
        result = result.substring(0, result.length() - 2);
        result = result.concat("]");
        return result;
    }
}
