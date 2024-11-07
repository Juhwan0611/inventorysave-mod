package com.juhwan.inventory;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class InventorySaveHandler {

    private final Item inventorySaveTicket;

    public InventorySaveHandler(Item inventorySaveTicket) {
        this.inventorySaveTicket = inventorySaveTicket;
    }

    public void handlePlayerDeath(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer) {
        // 인벤세이브권 아이템 개수 확인
        int saveTicketCount = 0;
        for (ItemStack stack : oldPlayer.getInventory().main) {
            if (stack.getItem() == inventorySaveTicket) {
                saveTicketCount += stack.getCount();
            }
        }

        if (saveTicketCount > 0) {
            // 인벤세이브권 하나 소모
            for (ItemStack stack : oldPlayer.getInventory().main) {
                if (stack.getItem() == inventorySaveTicket) {
                    stack.decrement(1);
                    break;
                }
            }

            // 인벤토리 복사
            newPlayer.getInventory().clone(oldPlayer.getInventory());
            newPlayer.sendMessage(Text.of("인벤세이브권을 사용하여 인벤토리를 보존했습니다."), false);
        } else {
            newPlayer.sendMessage(Text.of("인벤세이브권이 없어 인벤토리가 초기화되었습니다."), false);
        }
    }
}
