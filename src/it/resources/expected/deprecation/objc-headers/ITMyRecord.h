// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from deprecation.djinni

#import <Foundation/Foundation.h>

/**
 * record comment
 *
 * @deprecated Use someother record
 */
 __deprecated_msg("Use someother record")
@interface ITMyRecord : NSObject
- (nonnull instancetype)initWithAttribute:(nonnull NSString *)attribute
                                  another:(nonnull NSString *)another
                                    again:(nonnull NSString *)again;
+ (nonnull instancetype)myRecordWithAttribute:(nonnull NSString *)attribute
                                      another:(nonnull NSString *)another
                                        again:(nonnull NSString *)again;

/** @deprecated Use someother attribute */
@property (nonatomic, readonly, nonnull) NSString * attribute __deprecated_msg("Use someother attribute");

/** not deprecated */
@property (nonatomic, readonly, nonnull) NSString * another;

/** @deprecated Use someother attribute */
@property (nonatomic, readonly, nonnull) NSString * again __deprecated_msg("Use someother attribute");

@end

/** @deprecated Use someother constant */
extern int32_t const ITMyRecordVersion __deprecated_msg("Use someother constant");
