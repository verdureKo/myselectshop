package com.sparta.myselectshop.scheduler;

import com.sparta.myselectshop.entity.Product;
import com.sparta.myselectshop.naver.dto.ItemDto;
import com.sparta.myselectshop.naver.service.NaverApiService;
import com.sparta.myselectshop.repository.ProductRepository;
import com.sparta.myselectshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j(topic = "Scheduler")
@Component  // Bean으로 등록
@RequiredArgsConstructor
public class Scheduler {

    private final NaverApiService naverApiService;
    private final ProductService productService;
    private final ProductRepository productRepository;

    // cron: 특정한 시간에 특정한 작업을 예약
    // @Scheduled(cron = "*/10 * * * * *") // = "*/10 * * * * *" = every ten seconds
    @Scheduled(cron = "0 0 1 * * *") // = "*/10 * * * * *" = every 1:00 AM dawn
    public void updatePrice() throws InterruptedException {
        log.info("가격 업데이트 실행");
        List<Product> productList = productRepository.findAll();
        for (Product product : productList) {
            
            TimeUnit.SECONDS.sleep(1); // 1초에 한 상품 씩 조회합니다 (NAVER 제한있음)

            
            String title = product.getTitle(); // i 번째 관심 상품의 제목으로 검색 실행
            List<ItemDto> itemDtoList = naverApiService.searchItems(title);

            if (itemDtoList.size() > 0) {   // 1이라도 있다면
                ItemDto itemDto = itemDtoList.get(0); // i 번째 관심 상품 정보를 업데이트
                Long id = product.getId();
                try {
                    productService.updateBySearch(id, itemDto);
                } catch (Exception e) {
                    log.error(id + " : " + e.getMessage()); // 오류가 있더라도 예외처리(메세지로그)
                }
            }
        }
    }

}