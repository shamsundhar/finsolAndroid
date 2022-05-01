package com.finsol.tech.domain.model

import android.os.Parcelable
import com.finsol.tech.data.model.PortfolioData
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class PortfolioResponseDomainModel(val GetPortfolioResult: @RawValue ArrayList<PortfolioData>) : Parcelable