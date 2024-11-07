package com.juhwan.inventory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;

public class InventorySaveMod implements ModInitializer {

    // 인벤세이브권 아이템 정의
    public static final Item INVENTORY_SAVE_TICKET = new Item(new Item.Settings());

    @Override
    public void onInitialize() {
        // 아이템 등록
        Registry.register(Registries.ITEM, new Identifier("inventorysave", "inventory_save_ticket"), INVENTORY_SAVE_TICKET);

        // 플레이어가 죽을 때 아이템 드롭 방지
        ServerPlayerEvents.ALLOW_DEATH.register((player, damageSource, damageAmount) -> {
            if (player instanceof ServerPlayerEntity) {
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
                int saveTicketCount = 0;
                for (ItemStack stack : serverPlayer.getInventory().main) {
                    if (stack.getItem() == INVENTORY_SAVE_TICKET) {
                        saveTicketCount += stack.getCount();
                    }
                }

                // 인벤세이브권이 있을 경우 드롭 방지
                if (saveTicketCount > 0) {
                    return false; // 죽어도 아이템 드롭을 허용하지 않음
                }
            }
            return true; // 기본적인 아이템 드롭 허용
        });

        // 플레이어가 죽었을 때 인벤세이브권 사용 기능 추가
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            if (!alive) {
                // 인벤세이브권 아이템 개수 확인
                int saveTicketCount = 0;
                for (ItemStack stack : oldPlayer.getInventory().main) {
                    if (stack.getItem() == INVENTORY_SAVE_TICKET) {
                        saveTicketCount += stack.getCount();
                    }
                }

                if (saveTicketCount > 0) {
                    // 인벤세이브권 하나 소모
                    for (ItemStack stack : oldPlayer.getInventory().main) {
                        if (stack.getItem() == INVENTORY_SAVE_TICKET) {
                            stack.decrement(1);
                            break;
                        }
                    }

                    // 인벤토리와 갑옷 슬롯 복사
                    newPlayer.getInventory().clone(oldPlayer.getInventory());
                    newPlayer.getInventory().armor.set(0, oldPlayer.getInventory().armor.get(0));
                    newPlayer.getInventory().armor.set(1, oldPlayer.getInventory().armor.get(1));
                    newPlayer.getInventory().armor.set(2, oldPlayer.getInventory().armor.get(2));
                    newPlayer.getInventory().armor.set(3, oldPlayer.getInventory().armor.get(3));
                    newPlayer.getInventory().offHand.set(0, oldPlayer.getInventory().offHand.get(0));

                    newPlayer.sendMessage(Text.of("인벤세이브권을 사용하여 인벤토리와 장비를 보존했습니다."), false);
                } else {
                    newPlayer.sendMessage(Text.of("인벤세이브권이 없어 인벤토리와 장비가 초기화되었습니다."), false);
                }
            }
        });
    }
}
