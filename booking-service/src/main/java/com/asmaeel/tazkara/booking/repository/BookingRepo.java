package com.asmaeel.tazkara.booking.repository;

import com.asmaeel.tazkara.booking.common.BookingStatus;
import com.asmaeel.tazkara.booking.model.entity.BookingEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepo extends JpaRepository<BookingEntity, UUID> , JpaSpecificationExecutor<BookingEntity> {
    @Query("""
            SELECT (
                COALESCE(
                    (SELECT SUM(b.quantity)
                     FROM BookingEntity b
                     WHERE b.attendeeId = :attendeeId
                       AND b.eventId = :eventId),0) + :requestedQuantity) <= 6
            """)
    boolean canBook(UUID attendeeId, UUID eventId, int requestedQuantity);


    Optional<BookingEntity> findByReferenceAndAttendeeId(String reference, UUID attendeeId);


    /**
     * pg_advisory_xact_lock هي ميثود مدمجة من نفس قاعدة البينات
     * نعمل مثل قفل بمفتاح مجدموج
     * السبب الدالة canBook ممكن ييجي طلبين لنفس الشخص وكل وحدة تعمل ترانزاكشن طبيعي جدا !
     * المشكلة بتصير انه نفس الطلب بده يتاكد من عدد الايتمات في كل سجل الحجز اذا فرضا عنده حجزين او ثلاث لنفس الافنت كل اشي متوقع
     * فرضا اول ثريد بفحص قيمة عدد الايتمز الموجودة ضمن اي حجز سابق اله ولتكن صفر
     * ثاني ثريد بفوت وبقرا نفس القيمة بظظبط فا النتيجة كانه حجز فوق المطلوب
     * الاول 4 وثاني 4
     * 4 + 4 = 8
     * تجاوز للحد ضمن العملية كلها وممكن تسبب مشاكل فيما بعد لان عملية الحجز عملية واحدة كاملة
     * حسب ما فهمت انه كل شخص اله قفل خاص فيه على مستوى القاعدة ومافي طلبات تداخل مع بعضها
     * */

    @Query(value = """
    SELECT pg_advisory_xact_lock(
        hashtext(CAST(:attendeeId AS text)),
        hashtext(CAST(:eventId AS text))
    )
    """, nativeQuery = true)
    void lockAttendeeEvent(@Param("attendeeId") UUID attendeeId,
                           @Param("eventId") UUID eventId);

    @Query("""
            SELECT b.id FROM BookingEntity b
            WHERE b.status = :status AND b.holdExpiresAt <= :now
            """)
    List<UUID> findExpiredIds(@Param("status") BookingStatus status,
                              @Param("now") Instant now,
                              Pageable pageable);
}
