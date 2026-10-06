package network.minespazio.spazioabilities.ability;

public enum AbilityType {
    NINJA_SHEAR("NINJA_SHEAR"),
    SWITCHER("SWITCHER"),
    RESISTANCE("RESISTANCE"),
    STRENGTH("STRENGTH"),
    SPEED("SPEED"),
    ROCKET("ROCKET"),
    WEB_TRAP("WEB_TRAP"),
    ABILITY_INHIBIT("ABILITY_INHIBIT"),
    THORNS_SHIELD("THORNS_SHIELD"),
    ANTI_REDSTONE("ANTI_REDSTONE");

    private final String configKey;

    AbilityType(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigKey() {
        return configKey;
    }

    public static AbilityType fromString(String text) {
        if (text == null) return null;
        for (AbilityType type : values()) {
            if (type.name().equalsIgnoreCase(text) || type.configKey.equalsIgnoreCase(text)) {
                return type;
            }
        }
        return null;
    }
}
