package org.matsim.viz.files.entities;

import org.junit.Test;

import static org.junit.Assert.assertNotEquals;

public class TaggableTest {

	@Test
	public void addTag_tagSummaryIsCalculated() {

		Taggable taggable = new TestableTaggable();

		taggable.addTag(createTag("some-id", "tag-name", "tag-type"));

		assertNotEquals("", taggable.getTagSummary());
	}

	@Test
	public void addTag_addMultipleTags_tagSummaryIsDifferent() {

		Taggable taggable = new TestableTaggable();

		taggable.addTag(createTag("some-id", "tag-name", "tag-type"));
		String tagSummaryAfterFirstTag = taggable.getTagSummary();
		taggable.addTag(createTag("some-other-id", "other-tag-name", "tag-type"));

		assertNotEquals(tagSummaryAfterFirstTag, taggable.getTagSummary());
	}

	@Test
	public void removeTag_tagSummaryIsUpdated() {

		Taggable taggable = new TestableTaggable();
		Tag tag1 = createTag("some-id", "tag-name", "tag-type");
		Tag tag2 = createTag("some-other-id", "other-tag-name", "tag-type");
		taggable.addTag(tag1);
		taggable.addTag(tag2);
		String tagSummaryAfterSecondTag = taggable.getTagSummary();

		taggable.removeTag(tag1);

		assertNotEquals(tagSummaryAfterSecondTag, taggable.getTagSummary());
		String tagSummaryAfterRemovingFirstTag = taggable.getTagSummary();

		taggable.removeTag(tag2);
		assertNotEquals(tagSummaryAfterRemovingFirstTag, taggable.getTagSummary());
	}

	private Tag createTag(String id, String name, String type) {
		Tag tag = new Tag(name, type, new Project());
		tag.setId(id);
		return tag;
	}

	private static class TestableTaggable extends Taggable {

	}
}
