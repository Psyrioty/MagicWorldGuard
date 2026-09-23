package org.psyrioty.magicWorldGuard.Utils;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.config.WorldConfiguration;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class RegionCreator {
    public static ProtectedRegion createRegion(Player player, String id) {
        try {
            RegionManager manager = WorldGuard.getInstance()
                    .getPlatform()
                    .getRegionContainer()
                    .get(BukkitAdapter.adapt(player.getWorld()));

            if (manager == null) {
                return null;
            }

            LocalPlayer localPlayer = WorldGuardPlugin.inst()
                    .wrapPlayer(player);

            // Получаем выделение FAWE / WorldEdit
            LocalSession session = WorldEdit.getInstance()
                    .getSessionManager()
                    .get(BukkitAdapter.adapt(player));

            Region selection = session.getSelection(
                    BukkitAdapter.adapt(player.getWorld())
            );

            if (selection == null) {
                player.sendMessage("§cСначала сделайте выделение.");
                return null;
            }

            BlockVector3 min = selection.getMinimumPoint();
            BlockVector3 max = selection.getMaximumPoint();

            // =========================================================
            // Проверки WorldGuard
            // OP полностью игнорирует лимиты
            // =========================================================

            if (!player.isOp()) {

                WorldConfiguration config = WorldGuard.getInstance()
                        .getPlatform()
                        .getGlobalStateManager()
                        .get(BukkitAdapter.adapt(player.getWorld()));

                // Проверка количества приватов
                int currentRegions = manager.getRegionCountOfPlayer(localPlayer);
                int maxRegions = config.getMaxRegionCount(localPlayer);

                if (currentRegions >= maxRegions) {
                    player.sendMessage(
                            "§cВы достигли максимального количества приватов: §f"
                                    + maxRegions
                    );
                    return null;
                }

                // Размер выделения
                long sizeX = (long) max.x() - min.x() + 1;
                long sizeY = (long) max.y() - min.y() + 1;
                long sizeZ = (long) max.z() - min.z() + 1;

                long volume = sizeX * sizeY * sizeZ;

                // Лимит блоков WorldGuard
                long maxVolume = config.maxClaimVolume;

                if (volume > maxVolume) {
                    player.sendMessage(
                            "§cВыделение слишком большое!\n" +
                                    "§7Размер: §f" + volume + " §7блоков\n" +
                                    "§7Максимум: §f" + maxVolume + " §7блоков"
                    );
                    return null;
                }
            }

            // =========================================================
            // Создание региона
            // =========================================================

            ProtectedCuboidRegion region =
                    new ProtectedCuboidRegion(id, min, max);

            region.getOwners().addPlayer(player.getUniqueId());

            manager.addRegion(region);
            manager.save();

            return region;

        } catch (IncompleteRegionException e) {
            player.sendMessage("§cСначала сделайте корректное выделение.");
            return null;

        } catch (StorageException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ProtectedRegion createSubregion(Player player, String id) {
        try {
            RegionManager manager = WorldGuard.getInstance()
                    .getPlatform()
                    .getRegionContainer()
                    .get(BukkitAdapter.adapt(player.getWorld()));

            if (manager == null) {
                return null;
            }

            LocalSession session = WorldEdit.getInstance()
                    .getSessionManager()
                    .get(BukkitAdapter.adapt(player));

            Region selection = session.getSelection(
                    BukkitAdapter.adapt(player.getWorld())
            );

            if (selection == null) {
                player.sendMessage("§cСначала сделайте выделение.");
                return null;
            }

            BlockVector3 min = selection.getMinimumPoint();
            BlockVector3 max = selection.getMaximumPoint();

            ProtectedRegion parentRegion = null;

            for (ProtectedRegion region : manager.getRegions().values()) {

                // Регион должен принадлежать игроку
                if (!region.getOwners().contains(player.getUniqueId())) {
                    continue;
                }

                // Подприват не может быть родителем
                if (region.getParent() != null) {
                    continue;
                }

                // Выделение должно полностью находиться внутри региона
                if (!region.contains(min) || !region.contains(max)) {
                    continue;
                }

                // Если подходит несколько основных приватов,
                // выбираем самый маленький
                if (parentRegion == null) {
                    parentRegion = region;
                } else if (getRegionVolume(region) < getRegionVolume(parentRegion)) {
                    parentRegion = region;
                }
            }

            if (parentRegion == null) {
                player.sendMessage(
                        "§cВыделение не находится полностью внутри вашего основного региона."
                );
                return null;
            }

            ProtectedCuboidRegion subregion =
                    new ProtectedCuboidRegion(id, min, max);

            // Владелец
            subregion.getOwners().addPlayer(player.getUniqueId());

            // Родитель — только основной регион
            subregion.setParent(parentRegion);

            // Приоритет выше родителя
            subregion.setPriority(parentRegion.getPriority() + 1);

            manager.addRegion(subregion);
            manager.save();

            return subregion;

        } catch (IncompleteRegionException e) {
            player.sendMessage("§cСначала сделайте корректное выделение.");
            return null;

        } catch (StorageException e) {
            e.printStackTrace();
            return null;
        } catch (ProtectedRegion.CircularInheritanceException e) {
            throw new RuntimeException(e);
        }
    }

    private static long getRegionVolume(ProtectedRegion region) {
        BlockVector3 min = region.getMinimumPoint();
        BlockVector3 max = region.getMaximumPoint();

        long x = (long) max.x() - min.x() + 1;
        long y = (long) max.y() - min.y() + 1;
        long z = (long) max.z() - min.z() + 1;

        return x * y * z;
    }

    public static ProtectedRegion createRegionOrSubregion(Player player, String id) {
        try {
            LocalSession session = WorldEdit.getInstance()
                    .getSessionManager()
                    .get(BukkitAdapter.adapt(player));

            Region selection = session.getSelection(
                    BukkitAdapter.adapt(player.getWorld())
            );

            if (selection == null) {
                player.sendMessage("§cСначала сделайте выделение.");
                return null;
            }

            BlockVector3 min = selection.getMinimumPoint();
            BlockVector3 max = selection.getMaximumPoint();

            RegionManager manager = WorldGuard.getInstance()
                    .getPlatform()
                    .getRegionContainer()
                    .get(BukkitAdapter.adapt(player.getWorld()));

            if (manager == null) {
                player.sendMessage("§cНе удалось загрузить данные регионов.");
                return null;
            }

            // =========================================================
            // Проверка: регион с таким id уже существует?
            // =========================================================

            if (manager.hasRegion(id)) {
                player.sendMessage("§cРегион с названием \"" + id + "\" уже существует. Выберите другое имя.");
                return null;
            }

            // =========================================================
            // Поиск основного привата, внутри которого выделение
            // =========================================================

            ProtectedRegion parentRegion = null;

            for (ProtectedRegion region : manager.getRegions().values()) {

                // Только регионы без родителя
                if (region.getParent() != null) {
                    continue;
                }

                // Регион должен принадлежать игроку
                if (!region.getOwners().contains(player.getUniqueId())) {
                    continue;
                }

                // Выделение должно полностью находиться внутри региона
                if (!region.contains(min) || !region.contains(max)) {
                    continue;
                }

                // Если подходит несколько — берём самый маленький
                if (parentRegion == null
                        || getRegionVolume(region) < getRegionVolume(parentRegion)) {
                    parentRegion = region;
                }
            }

            // =========================================================
            // Внутри основного региона -> создаём подприват
            // =========================================================

            if (parentRegion != null) {
                return createSubregion(player, id);
            }

            // =========================================================
            // Не внутри основного региона -> обычный приват
            // =========================================================

            return createRegion(player, id);

        } catch (IncompleteRegionException e) {
            player.sendMessage("§cСначала сделайте корректное выделение.");
            return null;
        }
    }

    public static void kickMember(
            Player owner,
            OfflinePlayer member,
            ProtectedRegion protectedRegion
    ) {
        LocalPlayer localOwner = WorldGuardPlugin.inst().wrapPlayer(owner);

        if (!protectedRegion.isOwner(localOwner)) {
            owner.sendMessage("§cВы не являетесь владельцем этого региона.");
            return;
        }

        if (!protectedRegion.getMembers().contains(member.getUniqueId())) {
            owner.sendMessage("§cЭтот игрок не является участником региона.");
            return;
        }

        protectedRegion.getMembers().removePlayer(member.getUniqueId());

        RegionManager regionManager = WorldGuard.getInstance()
                .getPlatform()
                .getRegionContainer()
                .get(BukkitAdapter.adapt(owner.getWorld()));

        if (regionManager == null) {
            owner.sendMessage("§cНе удалось получить менеджер регионов.");
            return;
        }

        try {
            regionManager.save();
        } catch (StorageException e) {
            owner.sendMessage("§cНе удалось сохранить изменения.");
            e.printStackTrace();
            return;
        }

        owner.sendMessage(
                "§aИгрок §f" + member.getName() + " §aудалён из региона."
        );
    }

    public static ProtectedRegion getRegion(String name, World world) {
        RegionManager regionManager = WorldGuard.getInstance()
                .getPlatform()
                .getRegionContainer()
                .get(BukkitAdapter.adapt(world));

        if (regionManager == null) {
            return null;
        }

        return regionManager.getRegion(name);
    }

    public static void addMember(
            Player owner,
            OfflinePlayer member,
            String regionName
    ) {
        // 1. Получаем мир
        World world = owner.getWorld();

        // 2. Получаем менеджер регионов
        RegionManager regionManager = WorldGuard.getInstance()
                .getPlatform()
                .getRegionContainer()
                .get(BukkitAdapter.adapt(world));

        if (regionManager == null) {
            owner.sendMessage("§cНе удалось загрузить данные регионов для мира \"" + world.getName() + "\".");
            return;
        }

        // 3. Получаем регион
        ProtectedRegion region = regionManager.getRegion(regionName);
        if (region == null) {
            owner.sendMessage("§cРегион \"" + regionName + "\" не найден в мире \"" + world.getName() + "\".");
            return;
        }

        // 4. ⚠️ Проверяем, что игрок — владелец региона (или имеет право обхода)
        LocalPlayer localPlayerOwner = WorldGuardPlugin.inst()
                .wrapPlayer(owner);

        boolean isOwner = region.isOwner(localPlayerOwner);
        boolean hasBypass = owner.hasPermission("magicworldguard.admin")
                || owner.hasPermission("worldguard.region.addmember.own.*"); // при желании

        if (!isOwner && !hasBypass) {
            owner.sendMessage("§cВы не являетесь владельцем региона \"" + regionName + "\" и не можете управлять его участниками.");
            return;
        }

        // 5. Получаем домен участников
        DefaultDomain members = region.getMembers();

        // 6. Проверяем, не является ли игрок уже участником
        if (members.contains(member.getUniqueId())) {
            owner.sendMessage("§eИгрок " + member.getName() + " уже является участником региона \"" + regionName + "\".");
            return;
        }

        // 7. Добавляем игрока по UUID
        members.addPlayer(member.getUniqueId());

        // 8. Применяем изменения
        region.setMembers(members);

        // 9. Сообщаем об успехе
        owner.sendMessage("§aИгрок " + member.getName() + " добавлен в регион \"" + regionName + "\" в мире \"" + world.getName() + "\".");
    }

    public static void createRegionClick(Player player) {
        // ────────────────────────────────────────────────
        // Шапка
        // ────────────────────────────────────────────────
        player.sendMessage(MiniMessage.miniMessage().deserialize(
                "\n<gold>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                        "<yellow><bold>📖 Руководство по созданию привата\n" +
                        "<gold>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n"
        ));

        // ────────────────────────────────────────────────
        // Шаг 1 — Выделение территории
        // ────────────────────────────────────────────────
        player.sendMessage(MiniMessage.miniMessage().deserialize(
                "<gold>【 Шаг 1 】 <yellow>Выделение территории\n" +
                        "<gray>Возьмите <white>деревянный топор<gray> в руку.\n" +
                        "<gray>• <white>ЛКМ<gray> — первый угол\n" +
                        "<gray>• <white>ПКМ<gray> — второй угол\n" +
                        "<gray>Если топора нет — введите <click:suggest_command:'//wand'>" +
                        "<hover:show_text:'<gray>Нажми, чтобы подставить'><green><u>//wand</u></green></click>\n"
        ));

        // ────────────────────────────────────────────────
        // Шаг 2 — Создание региона
        // ────────────────────────────────────────────────
        player.sendMessage(MiniMessage.miniMessage().deserialize(
                "<gold>【 Шаг 2 】 <yellow>Создание привата\n" +
                        "<gray>После выделения введите команду:\n" +
                        "<click:suggest_command:'/rg claim '>" +
                        "<hover:show_text:'<gray>Нажми, впиши название и Enter'>" +
                        "<green><u>/rg claim <название></u></green></click>\n" +
                        "<gray>Вы автоматически станете владельцем региона.\n"
        ));

        // ────────────────────────────────────────────────
        // Шаг 3 — Подприват (регион внутри региона)
        // ────────────────────────────────────────────────
        player.sendMessage(MiniMessage.miniMessage().deserialize(
                "<gold>【 Шаг 3 】 <yellow>Подприват (регион внутри региона)\n" +
                        "<gray>Подприват нужен, чтобы защитить отдельную зону внутри\n" +
                        "<gray>основного привата (например, чей-то дом на территории города).\n\n" +
                        "<gray>1. Выделите территорию <white>внутри<gray> основного привата.\n" +
                        "<gray>2. Создайте регион:\n" +
                        "<click:suggest_command:'/rg claim '>" +
                        "<hover:show_text:'<gray>Нажми, впиши название и Enter'>" +
                        "<green><u>/rg claim <название_подрегиона></u></green></click>\n"
        ));

        // ────────────────────────────────────────────────
        // Полезные команды
        // ────────────────────────────────────────────────
        player.sendMessage(MiniMessage.miniMessage().deserialize(
                "<gold>【 Полезные команды 】\n" +
                        "<click:suggest_command:'/rg addmember '>" +
                        "<hover:show_text:'<gray>Добавить участника'>" +
                        "<green><u>/rg addmember <регион> <ник></u></green></click> <gray>— добавить игрока\n" +
                        "<click:suggest_command:'/rg'>" +
                        "<hover:show_text:'<gray>Основное меню'>" +
                        "<green><u>/rg</u></green></click> <gray>— Основное меню\n"
        ));

        // ────────────────────────────────────────────────
        // Подвал
        // ────────────────────────────────────────────────
        player.sendMessage(MiniMessage.miniMessage().deserialize(
                "<gold>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                        "<gray>Нажмите на <green>зелёную команду<gray>, чтобы подставить её в чат.\n" +
                        "<gold>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n"
        ));
    }
}
