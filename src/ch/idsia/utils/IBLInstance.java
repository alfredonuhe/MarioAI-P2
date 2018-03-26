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

    /*TODO: delete if not neccessary*/
    public String toString() {
        String result = "instanceGroup: " + instanceGroup + ", attributeArray: [ ";
        for (int i = 0; i < attributeArray.length; i++) {
            result.concat('"' + attributeArray[i] + "\", ");
        }
        result.concat("]");
        return result;
    }
}
