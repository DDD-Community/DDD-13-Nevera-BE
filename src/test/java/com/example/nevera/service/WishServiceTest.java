package com.example.nevera.service;

import com.example.nevera.common.exception.BusinessException;
import com.example.nevera.dto.wish.WishRequest;
import com.example.nevera.dto.wish.WishResponse;
import com.example.nevera.entity.Member;
import com.example.nevera.entity.WishEntity;
import com.example.nevera.repository.MemberRepository;
import com.example.nevera.repository.WishRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WishServiceTest {

    @Mock
    private WishRepository wishRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private WishService wishService;

    private static final Long MEMBER_ID = 1L;

    private Member member() {
        return Member.builder().email("test@test.com").build();
    }

    private WishEntity wishEntity(String name, int amount) {
        return WishEntity.builder().member(member()).name(name).amount(amount).build();
    }

    // ── wish 등록 ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("wish 등록 시 기존 wish가 모두 삭제된 후 새로 저장된다")
    void register_deletesExistingAndSavesNew() {
        WishRequest request = new WishRequest("노트북", 1_500_000);
        given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(member()));
        given(wishRepository.save(any(WishEntity.class))).willReturn(wishEntity("노트북", 1_500_000));

        WishResponse result = wishService.register(MEMBER_ID, request);

        verify(wishRepository, times(1)).deleteAllByMemberId(MEMBER_ID);
        verify(wishRepository, times(1)).save(any(WishEntity.class));
        assertThat(result.name()).isEqualTo("노트북");
        assertThat(result.amount()).isEqualTo(1_500_000);
    }

    @Test
    @DisplayName("wish 재등록 시 기존 wish가 삭제되고 새 wish로 교체된다")
    void register_replacesExistingWish() {
        WishRequest newRequest = new WishRequest("카메라", 800_000);
        given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.of(member()));
        given(wishRepository.save(any(WishEntity.class))).willReturn(wishEntity("카메라", 800_000));

        WishResponse result = wishService.register(MEMBER_ID, newRequest);

        verify(wishRepository, times(1)).deleteAllByMemberId(MEMBER_ID);
        assertThat(result.name()).isEqualTo("카메라");
        assertThat(result.amount()).isEqualTo(800_000);
    }

    @Test
    @DisplayName("존재하지 않는 회원으로 wish 등록 시 예외가 발생한다")
    void register_memberNotFound() {
        given(memberRepository.findById(MEMBER_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> wishService.register(MEMBER_ID, new WishRequest("노트북", 1_500_000)))
                .isInstanceOf(BusinessException.class);
    }

    // ── wish 조회 ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("wish 없을 때 getCurrent는 Optional.empty를 반환한다")
    void getCurrent_noWish() {
        given(wishRepository.findTopByMemberIdOrderByCreatedAtDesc(MEMBER_ID)).willReturn(Optional.empty());

        assertThat(wishService.getCurrent(MEMBER_ID)).isEmpty();
    }

    @Test
    @DisplayName("wish 있을 때 getCurrent는 현재 wish를 반환한다")
    void getCurrent_withWish() {
        given(wishRepository.findTopByMemberIdOrderByCreatedAtDesc(MEMBER_ID))
                .willReturn(Optional.of(wishEntity("노트북", 1_500_000)));

        Optional<WishResponse> result = wishService.getCurrent(MEMBER_ID);

        assertThat(result).isPresent();
        assertThat(result.get().name()).isEqualTo("노트북");
        assertThat(result.get().amount()).isEqualTo(1_500_000);
    }
}
