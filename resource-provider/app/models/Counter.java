package models;

import lombok.Data;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Data
@Entity(value = "counters", noClassnameStored = true)
public class Counter {

  @Id
  private ObjectId _id;
  private String collection;
  private Long seq;

}
