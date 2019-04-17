package org.matsim.viz.files.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
public abstract class Taggable extends Resource {

	private static DigestUtils digestUtils = new DigestUtils(MessageDigestAlgorithms.SHA_256);

	@ManyToMany(fetch = FetchType.EAGER)
	private Set<Tag> tags = new HashSet<>();

	@JsonIgnore
	@Column(nullable = false, length = 64)
	private String tagSummary = "";

	void addTag(Tag tag) {
		this.tags.add(tag);
		updateTagSummary();
	}

	public void addTags(String[] tagIds) {
		for (String tagId : tagIds) {
			Tag tag = new Tag();
			tag.setId(tagId);
			this.addTag(tag);
		}
	}

	void removeTag(Tag tag) {
		this.tags.remove(tag);
		updateTagSummary();
	}

	private void updateTagSummary() {
		final String allTags = tags.stream()
				.sorted((tag1, tag2) -> tag1.getId().compareToIgnoreCase(tag2.getId()))
				.map(Tag::getId)
				.collect(Collectors.joining());
		this.tagSummary = digestUtils.digestAsHex(allTags);
	}
}
