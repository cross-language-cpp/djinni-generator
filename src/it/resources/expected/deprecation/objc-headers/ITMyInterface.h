// AUTOGENERATED FILE - DO NOT MODIFY!
// This file was generated by Djinni from deprecation.djinni

#import <Foundation/Foundation.h>

/** @deprecated Use someother constant */
extern int32_t const ITMyInterfaceVersion __deprecated_msg("Use someother constant");

/**
 * interface comment
 *
 * @deprecated Use someother interface
 */
 __deprecated_msg("Use someother interface")
@interface ITMyInterface : NSObject

/** @deprecated Use someother method */
- (void)methodA:(int32_t)value __deprecated_msg("Use someother method");

/** @deprecated Use someother method */
- (void)methodB:(int32_t)value __deprecated_msg("Use someother method");

/** @deprecated Use someother method */
+ (void)methodC:(int32_t)value __deprecated_msg("Use someother method");

/** not deprecated */
- (void)methodD;

/** really im not */
- (void)methodE;

@end
