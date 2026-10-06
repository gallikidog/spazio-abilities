# SpazioAbilities - HCF Combat Partner Items & Abilities Core

**SpazioAbilities** es un plugin de habilidades de combate estilo **HCF / Partner Items** diseñado para **Paper 1.21.1+** con integración nativa y protección de zonas PvP mediante **WorldGuard**.

---

## 🌟 Características Principales

- ⚔️ **10 Habilidades de Combate HCF Integradas**:
  1. ⚡ **Ninja Shear (`NINJA_SHEAR`)**: Te teletransporta inmediatamente detrás del último jugador que te atacó en los últimos 15 segundos.
  2. 🔀 **Switcher Egg (`SWITCHER`)**: Intercambia posiciones al instante con un jugador enemigo dentro de un rango de 12 bloques.
  3. 🛡️ **Portable Resistance (`RESISTANCE`)**: Otorga **Resistencia III** y **Absorción II** durante 6 segundos.
  4. ⚡ **Portable Strength (`STRENGTH`)**: Otorga **Fuerza II** durante 5 segundos.
  5. 💨 **Portable Speed (`SPEED`)**: Otorga **Velocidad III** durante 8 segundos.
  6. 🚀 **Rocket Boost (`ROCKET`)**: Impulsa al jugador verticalmente por los aires otorgándole inmunidad total al daño de caída.
  7. 🕸️ **Cobweb Trap (`WEB_TRAP`)**: Atrapa al enemigo en una jaula de telaraña de 3x3 por 5 segundos que se limpia automáticamente.
  8. 🔒 **Ability Inhibitor (`ABILITY_INHIBIT`)**: Bloquea el uso de todas las habilidades de combate del objetivo durante 10 segundos.
  9. 🛡️ **Thorns Shield (`THORNS_SHIELD`)**: Refleja el 50% del daño físico recibido directamente al atacante durante 6 segundos.
  10. 💥 **Anti-Redstone Disruptor (`ANTI_REDSTONE`)**: Elimina al instante todos los efectos de pociones positivas del objetivo.

- 🛡️ **Integración Avanzada con WorldGuard (PvP Allowed Region Guard)**:
  - Verifica si el usuario y el objetivo están dentro de una zona donde el flag `pvp` de WorldGuard esté activado (`ALLOW`).
  - **REGLA PvP DE SEGURIDAD**: Si un jugador está dentro de la zona PvP pero su enemigo se encuentra fuera (o en SafeZone/No-PvP), la habilidad **NO funcionará** y mostrará una advertencia impidiendo el uso desigual de ítems de combate.

- ⏳ **Sistema Doble de Cooldown**:
  - Cooldown individual para cada habilidad.
  - Cooldown Global personalizable (ej. 3 segundos entre uso de cualquier habilidad).

- 🎒 **Identificación por PersistentDataContainer (PDC)**:
  - Cada ítem contiene un NBT/PDC exclusivo (`spazio_ability_id`), impidiendo que jugadores usen ítems ordinarios renombraos o crafteados en yunque.

- 🖥️ **GUI Interactiva de Administración (`/ability gui`)**:
  - Panel visual para obtener cualquier objeto de habilidad con un solo click.

---

## 📜 Lista Completa de Comandos y Alias

| Comando | Alias | Descripción | Permiso |
| :--- | :--- | :--- | :--- |
| `/spazioabilities give <jugador> <habilidad> [cantidad]` | `/ability give`, `/partneritems give` | Otorga una habilidad de combate especificada a un jugador. | `me.spazioabilities.admin` |
| `/spazioabilities gui` | `/ability gui`, `/partneritem gui` | Abre el menú GUI visual para conseguir cualquier ítem de habilidad. | `me.spazioabilities.admin` |
| `/spazioabilities list` | `/ability list` | Muestra la lista de todas las habilidades registradas. | `me.spazioabilities.admin` |
| `/spazioabilities reload` | `/ability reload` | Recarga la configuración del plugin (`config.yml`). | `me.spazioabilities.admin` |

---

## 🔑 Tabla Completa de Permisos

| Permiso | Descripción | Por Defecto |
| :--- | :--- | :--- |
| `me.spazioabilities.admin` | Acceso completo a todos los comandos de administración y obtención de ítems. | `OP` |
| `me.spazioabilities.use` | Permite activar y usar los ítems de habilidades en combate. | `true` |

---

## 🛠️ Compilación e Instalación

1. Requisitos: JDK 21 o superior y Maven.
2. Compilación:
   ```bash
   mvn clean package
   ```
3. El archivo JAR compilado estará disponible en:
   `target/spazioabilities-1.0.0.jar`
