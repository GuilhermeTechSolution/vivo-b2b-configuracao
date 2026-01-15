package br.com.iatapp.helper;

public enum GlobalStrEnum {
	
	IPADDRESS_PATTERN("(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)"),
	HOSTNAME_PATTERN("([A-Za-z0-9]{1,4}[\\-])*([A-Za-z0-9]{1,4}[\\-]){3}([A-Za-z0-9]{1,4})"),
	INTERFACE_PE_PATTERN("([a-zA-Z0-9]+?\\/.+?\\.\\d{7})"),
	MAC_ADDRESS_PATTERN("([0-9a-fA-F][0-9a-fA-F]:){5}([0-9a-fA-F][0-9a-fA-F])"),
	BREAK_LINE("(?:\\[rn]|[\r\n]+)+"),
	NUMBERS ("(\\d+)"),
	NUMBERS_EX(".*[0-9].*"),
	INTERFACE_PE_SERIAL_PATTERN("(serial\\d)|(se\\d)"),
	INTERFACE_PE_MULTILINK_PATTERN("(multilink\\d)|(mu\\d)"),
	INTERFACE_PE_SERIAL_FULL_PATTERN("(serial\\d.+? )|(se\\d.+? )"),
	E1_PATTERN("(E1\\d)"),
	END_OF_STRING("endOfString"),
	ALL_SEQ_SPACES("^ +| +$|( )+"),
	INTERFACE_FISICA("(((\\d\\/)|(\\d{1,2}\\/)){1,4}(\\d){1,3})"),
	SWT_INTERFACE_TIPO_FISICA("((Gi +)|(Gi)|(GigabitEthernet +)|(GigabitEthernet)|(te +)|(te)|(tenGigabiEthernet +)|(tenGigabiEthernet)|(BE +)|(BE)|(Bundle-Ether +)|(Bundle-Ether)|(Po +)|(Po)|(Port-channel +)|(Port-channel)|(Eth-Trunk +)|(Eth-Trunk))((\\d{1,2}\\/){0,4}(\\d){1,4})"),
	SWT_INTERFACE_TIPO_FISICA_SERVICO_USUARIO("((Gi +)|(Gi)|(GigabitEthernet +)|(GigabitEthernet)|(te +)|(te)|(tenGigabiEthernet +)|(tenGigabiEthernet)|(BE +)|(BE)|(Bundle-Ether +)|(Bundle-Ether)|(Po +)|(Po)|(Port-channel +)|(Port-channel)|(Eth-Trunk +)|(Eth-Trunk))((\\d{1,2}\\/){0,4}(\\d){1,4}.(\\d){1,6})"),
	SWT_INTERFACE_AUX("([0-9]{1,4}\\/ *[0-9]{1,4})"),
	SWT_HOSTNAME_PATTERN("([A-Za-z0-9]{1,4}[\\-])*([A-Za-z0-9]{1,4}[\\-]){3}([A-Za-z0-9]{1,6})"),
	CLOCK_FORMAT("(([01]\\d|2[0-3]):[0-5]\\d:[0-5]\\d)"), // HH:MM:SS
	DATE_FORMAT_USA("(\\d{4}-\\d{2}-\\d{2})"), // YYYY-MM-DD
	DATE_HOUR_FORMAT_EXTRA("((\\d{1,3}[a-zA-Z]){2,4})"); // 22w1d | 2d22h | 1y19w2d
	
	private final String name;
	
	/**
	 * Construtores
	 */
    private GlobalStrEnum(String s) {
        name = s;
    }
	
	/**
	 * Métodos
	 */
    // Compara string como o valor de enum
    public boolean equalsName(String otherName) {
        return (otherName == null) ? false : name.equals(otherName);
    }

    // Retorno o valor de enum
    public String toString() {
       return this.name;
    }
}
