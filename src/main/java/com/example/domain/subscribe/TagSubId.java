package com.example.domain.subscribe;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TagSubId implements Serializable {
    private Long memberId;
    private Long tagId;

    public TagSubId() {}

    public TagSubId(Long memberId, Long tagId) {
        this.memberId = memberId;
        this.tagId = tagId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagSubId tagSubId = (TagSubId) o;
        return Objects.equals(memberId, tagSubId.memberId) &&
                Objects.equals(tagId, tagSubId.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, tagId);
    }
}
