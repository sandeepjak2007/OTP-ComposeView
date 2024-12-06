package com.sandeep.composeotp

sealed interface OtpAction {
    data class OnEnterNumber(val number: Int?, val index: Int) : OtpAction
    data class OnFieldChangedFocused(val index: Int) : OtpAction
    data object OnKeyboardBack : OtpAction
}