// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: VisionField.proto

package messages;

public interface VisualLineOrBuilder extends
    // @@protoc_insertion_point(interface_extends:messages.VisualLine)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>optional .messages.VisualDetection visual_detection = 1;</code>
   */
  boolean hasVisualDetection();
  /**
   * <code>optional .messages.VisualDetection visual_detection = 1;</code>
   */
  messages.VisualDetection getVisualDetection();
  /**
   * <code>optional .messages.VisualDetection visual_detection = 1;</code>
   */
  messages.VisualDetectionOrBuilder getVisualDetectionOrBuilder();

  /**
   * <code>optional int32 start_x = 2;</code>
   */
  boolean hasStartX();
  /**
   * <code>optional int32 start_x = 2;</code>
   */
  int getStartX();

  /**
   * <code>optional int32 start_y = 3;</code>
   */
  boolean hasStartY();
  /**
   * <code>optional int32 start_y = 3;</code>
   */
  int getStartY();

  /**
   * <code>optional float start_dist = 12;</code>
   */
  boolean hasStartDist();
  /**
   * <code>optional float start_dist = 12;</code>
   */
  float getStartDist();

  /**
   * <code>optional float start_bearing = 13;</code>
   */
  boolean hasStartBearing();
  /**
   * <code>optional float start_bearing = 13;</code>
   */
  float getStartBearing();

  /**
   * <code>optional int32 end_x = 4;</code>
   */
  boolean hasEndX();
  /**
   * <code>optional int32 end_x = 4;</code>
   */
  int getEndX();

  /**
   * <code>optional int32 end_y = 5;</code>
   */
  boolean hasEndY();
  /**
   * <code>optional int32 end_y = 5;</code>
   */
  int getEndY();

  /**
   * <code>optional float end_dist = 14;</code>
   */
  boolean hasEndDist();
  /**
   * <code>optional float end_dist = 14;</code>
   */
  float getEndDist();

  /**
   * <code>optional float end_bearing = 15;</code>
   */
  boolean hasEndBearing();
  /**
   * <code>optional float end_bearing = 15;</code>
   */
  float getEndBearing();

  /**
   * <code>optional float angle = 6;</code>
   */
  boolean hasAngle();
  /**
   * <code>optional float angle = 6;</code>
   */
  float getAngle();

  /**
   * <code>optional float avg_width = 7;</code>
   */
  boolean hasAvgWidth();
  /**
   * <code>optional float avg_width = 7;</code>
   */
  float getAvgWidth();

  /**
   * <code>optional float length = 8;</code>
   */
  boolean hasLength();
  /**
   * <code>optional float length = 8;</code>
   */
  float getLength();

  /**
   * <code>optional float slope = 9;</code>
   */
  boolean hasSlope();
  /**
   * <code>optional float slope = 9;</code>
   */
  float getSlope();

  /**
   * <code>optional float y_int = 10;</code>
   */
  boolean hasYInt();
  /**
   * <code>optional float y_int = 10;</code>
   */
  float getYInt();

  /**
   * <code>repeated .messages.VisualLine.line_id possibilities = 11;</code>
   */
  java.util.List<messages.VisualLine.line_id> getPossibilitiesList();
  /**
   * <code>repeated .messages.VisualLine.line_id possibilities = 11;</code>
   */
  int getPossibilitiesCount();
  /**
   * <code>repeated .messages.VisualLine.line_id possibilities = 11;</code>
   */
  messages.VisualLine.line_id getPossibilities(int index);
}
