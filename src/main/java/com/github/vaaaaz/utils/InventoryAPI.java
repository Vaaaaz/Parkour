package com.github.vaaaaz.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class InventoryAPI {

    private BukkitTask task;
    private BukkitTask killTask;

    private final String title;
    private final Inventory inv;
    private int page = 1;
    private int pages = 1;
    private List<ItemStack> pageItens;
    private HashMap<Integer, ItemStack> guiItens;
    private final int[] slots;

    public InventoryAPI(int linhas, String title, int[] slots) {
        this.inv = Bukkit.createInventory(null, linhas * 9, title);
        this.title = title;
        this.slots = slots;
        this.pageItens = new ArrayList<>();
        this.guiItens = new HashMap<>();
    }

    public void update() { // 14 % 7 == 0 ? 1 : 14 / 7 + 1 = 2 (2.5)
        getInventory().clear();
        if (pageItens.size() > 0) {
            this.pages = pageItens.size() % slots.length == 0 ? pageItens.size() / slots.length :
                    pageItens.size() / slots.length + 1;
            for (int slot = 0; slot < getInventory().getSize(); slot++) {
                int finalSlot = slot;
                if (Arrays.stream(slots).noneMatch(a -> a == finalSlot)) {
                    getInventory().setItem(slot, new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 8));
                }
            }
            for (int item = page * slots.length - slots.length; item < pageItens.size(); item++) {
                ItemStack itemStack = pageItens.get(item);
                getInventory().addItem(itemStack);
            }
        }
        if (!getGuiItens().isEmpty()) {
            for (Integer k : getGuiItens().keySet()) {
                getInventory().setItem(k, getGuiItens().get(k));
            }
        }
        for (int slot = 0; slot < getInventory().getSize(); slot++) {
            if (!getGuiItens().containsKey(slot)) {
                if (getInventory().getItem(slot) != null) {
                    if (getInventory().getItem(slot).getType().equals(Material.STAINED_GLASS_PANE)) {
                        if (!getInventory().getItem(slot).getItemMeta().hasLore()) {
                            getInventory().setItem(slot, new ItemStack(Material.AIR));
                        }
                    }
                }
            }
        }
    }

    public int[] getSlots() {
        return slots;
    }

    public HashMap<Integer, ItemStack> getGuiItens() {
        return guiItens;
    }

    public void setGuiItens(HashMap<Integer, ItemStack> guiItens) {
        this.guiItens = guiItens;
    }

    public void setGuiItem(Integer slot, ItemStack guiItens) {
        this.guiItens.put(slot, guiItens);
    }

    public void next() {
        if (getPage() < getPages()) {
            setPage(getPage() + 1);
            update();
        }
    }

    public void previous() {
        if (getPage() <= getPages() && getPage() > 1) {
            setPage(getPage() - 1);
            update();
        }
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setPageItens(List<ItemStack> pageItens) {
        for (int i = 0; i < pageItens.size(); i++) {
            ItemMeta meta = Bukkit.getItemFactory().getItemMeta(pageItens.get(i).getType());
            if (pageItens.get(i).hasItemMeta()) {
                meta = pageItens.get(i).getItemMeta();
            }
            List<String> lore = new ArrayList<>();
            if (meta.hasLore()) {
                lore = meta.getLore();
            }
            boolean id = false;
            for (String loreLinha : lore) {
                if (loreLinha.startsWith("§0§0ID ")) {
                    id = true;
                    break;
                }
            }
            if (!id) {
                lore.add("§0§0ID " + i);

            }
            meta.setLore(lore);

            pageItens.get(i).setItemMeta(meta);
        }
        this.pageItens = pageItens;
    }

    public boolean isPageItem(int slot) {
        if (getInventory().getItem(slot) != null) {
            ItemStack itemStack = getInventory().getItem(slot);
            if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore()) {
                List<String> lore = itemStack.getItemMeta().getLore();
                String ultimaLinha = lore.get(lore.size() - 1);
                return ultimaLinha.startsWith("§0§0ID ");
            }
            return false;
        }
        return false;
    }

    public Integer getPageItemID(int slot) {
        if (getInventory().getItem(slot) != null) {
            ItemStack itemStack = getInventory().getItem(slot);
            if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore()) {
                List<String> lore = itemStack.getItemMeta().getLore();
                String ultimaLinha = lore.get(lore.size() - 1);
                if (ultimaLinha.startsWith("§0§0ID ")) {
                    return Integer.parseInt(ultimaLinha.split(" ")[1]);
                }
            }
        }
        return null;
    }

    public void addPageItem(ItemStack itemStack) {
        pageItens.add(itemStack);
    }

    public Inventory getInventory() {
        return inv;
    }

    public int getPage() {
        return page;
    }

    public List<ItemStack> getPageItens() {
        return pageItens;
    }

    public String getTitle() {
        return title;
    }

    public int getPages() {
        return pages;
    }

    public BukkitTask setTaskTimerAsync(BukkitRunnable bukkitRunnable, Plugin plugin, Long delay, Long period) {
        if (killTask == null) {
            task = bukkitRunnable.runTaskTimerAsynchronously(plugin, delay, period);
            taskKill(plugin);
            return task;
        }
        return null;
    }

    public BukkitTask setTaskLaterAsync(BukkitRunnable bukkitRunnable, Plugin plugin, Long delay) {
        if (killTask == null) {
            task = bukkitRunnable.runTaskLaterAsynchronously(plugin, delay);
            taskKill(plugin);
            return task;
        }
        return null;
    }

    public BukkitTask setTaskTimer(BukkitRunnable bukkitRunnable, Plugin plugin, Long delay, Long period) {
        if (killTask == null) {
            task = bukkitRunnable.runTaskTimer(plugin, delay, period);
            taskKill(plugin);
            return task;
        }
        return null;
    }

    public BukkitTask setTaskLater(BukkitRunnable bukkitRunnable, Plugin plugin, Long delay) {
        if (killTask == null) {
            task = bukkitRunnable.runTaskLater(plugin, delay);
            taskKill(plugin);
            return task;
        }
        return null;
    }

    private void taskKill(Plugin plugin) {
        if (killTask == null) {
            killTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if (getInventory().getViewers().size() == 0) {
                        task.cancel();
                        cancel();
                    }
                }
            }.runTaskTimerAsynchronously(plugin, 0L, 20L);
        }
    }

//        public static String hideText(String text) {
//            Objects.requireNonNull(text, "text can not be null!");
//
//            StringBuilder output = new StringBuilder();
//
//            byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
//            String hex = Hex.encodeHexString(bytes);
//
//            for (char c : hex.toCharArray()) {
//                output.append("§").append(c);
//            }
//            return output.toString();
//        }
//
//        public static String showText(@Nonnull String text) {
//            Objects.requireNonNull(text, "text can not be null!");
//            try {
//                byte[] bytes = Hex.decodeHex(text.replaceAll("§", "").toCharArray());
//                return new String(bytes, StandardCharsets.UTF_8);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return "";
//        }
}

